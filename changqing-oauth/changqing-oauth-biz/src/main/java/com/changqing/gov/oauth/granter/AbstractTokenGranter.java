package com.changqing.gov.oauth.granter;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.authority.entity.auth.Application;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.auth.UserToken;
import com.changqing.gov.authority.service.auth.ApplicationService;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.base.R;
import com.changqing.gov.boot.utils.WebUtils;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.database.properties.MultiTenantType;
import com.changqing.gov.exception.code.ExceptionCode;
import com.changqing.gov.jwt.TokenUtil;
import com.changqing.gov.jwt.model.AuthInfo;
import com.changqing.gov.jwt.model.JwtUserInfo;
import com.changqing.gov.jwt.utils.JwtUtil;
import com.changqing.gov.oauth.event.LoginEvent;
import com.changqing.gov.oauth.event.model.LoginStatusDTO;
import com.changqing.gov.oauth.utils.TimeUtils;
import com.changqing.gov.tenant.entity.Tenant;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.service.TenantService;
import com.changqing.gov.utils.BeanPlusUtil;
import com.changqing.gov.utils.BizAssert;
import com.changqing.gov.utils.DateUtils;
import com.changqing.gov.utils.SpringUtils;
import com.changqing.gov.utils.StrHelper;
import com.changqing.gov.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.changqing.gov.context.BaseContextConstants.BASIC_HEADER_KEY;
import static com.changqing.gov.utils.BizAssert.gt;
import static com.changqing.gov.utils.BizAssert.notNull;

/**
 * 验证码TokenGranter
 *
 * @author Chill
 */
@Slf4j
public abstract class AbstractTokenGranter implements TokenGranter {
    @Autowired
    protected TokenUtil tokenUtil;
    @Autowired
    protected UserService userService;
    @Autowired
    protected TenantService tenantService;
    @Autowired
    protected CacheChannel cacheChannel;
    @Autowired
    protected ApplicationService applicationService;
    @Autowired
    protected DatabaseProperties databaseProperties;

    /**
     * 处理登录逻辑
     *
     * @param loginParam 登录参数
     * @return 认证信息
     */
    protected R<AuthInfo> login(LoginParamDTO loginParam) {
        if (StrHelper.isAnyBlank(loginParam.getAccount(), loginParam.getPassword())) {
            return R.fail("请输入用户名或密码");
        }
        // 1，检测租户是否可用
        if (!MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType())) {
            Tenant tenant = this.tenantService.getByCode(loginParam.getTenant());
            notNull(tenant, "企业不存在");
            BizAssert.equals(TenantStatusEnum.NORMAL, tenant.getStatus(), "企业不可用~");
            if (tenant.getExpirationTime() != null) {
                gt(LocalDateTime.now(), tenant.getExpirationTime(), "企业服务已到期~");
            }
            BaseContextHandler.setTenant(tenant.getCode());
        }

        // 2.检测client是否可用
        R<String[]> checkR = checkClient();
        if (checkR.getIsError()) {
            return R.fail(checkR.getMsg());
        }

        // 3. 验证登录
        R<User> result = this.getUser(loginParam.getAccount(), loginParam.getPassword());
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }

        // 4.查询用户的权限
        User user = result.getData();

        // 5.生成 token
        AuthInfo authInfo = this.createToken(user);

        UserToken userToken = getUserToken(checkR.getData()[0], authInfo);

        //成功登录事件
        SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.success(user.getId(), userToken)));
        return R.success(authInfo);
    }

    private UserToken getUserToken(String clientId, AuthInfo authInfo) {
        UserToken userToken = new UserToken();
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("userId", "createUser");
        BeanPlusUtil.copyProperties(authInfo, userToken, CopyOptions.create().setFieldMapping(fieldMapping));
        userToken.setClientId(clientId);
        userToken.setExpireTime(DateUtils.date2LocalDateTime(authInfo.getExpiration()));
        return userToken;
    }


    /**
     * 检测 client
     *
     * @return
     */
    protected R<String[]> checkClient() {
        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        String[] client = JwtUtil.getClient(basicHeader);
        Application application = applicationService.getByClient(client[0], client[1]);

        if (application == null) {
            return R.fail("请填写正确的客户端ID或者客户端秘钥");
        }
        if (!application.getStatus()) {
            return R.fail("客户端[%s]已被禁用", application.getClientId());
        }
        return R.success(client);
    }


    /**
     * 检测用户密码是否正确
     *
     * @param account  账号
     * @param password 密码
     * @return 用户信息
     */
    protected R<User> getUser(String account, String password) {
        User user = this.userService.getByAccount(account);
        // 密码错误
        String passwordMd5 = cn.hutool.crypto.SecureUtil.md5(password);
        if (user == null) {
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }

        System.out.println("password="+user.getPassword());

        if (!user.getPassword().equalsIgnoreCase(passwordMd5)) {
            String msg = "用户名或密码错误!";
            // 密码错误事件
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.pwdError(user.getId(), msg)));
            return R.fail(msg);
        }

        // 密码过期
        if (user.getPasswordExpireTime() != null && LocalDateTime.now().isAfter(user.getPasswordExpireTime())) {
            String msg = "用户密码已过期，请修改密码或者联系管理员重置!";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        if (!user.getStatus()) {
            String msg = "用户被禁用，请联系管理员！";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }
        System.out.println(user.toString());
        if (user.getIsDelete()!=null&&user.getIsDelete()) {
            String msg = "用户被删除，请联系管理员！";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        // 用户锁定
        Integer maxPasswordErrorNum = 0;
        Integer passwordErrorNum = Convert.toInt(user.getPasswordErrorNum(), 0);
        if (maxPasswordErrorNum > 0 && passwordErrorNum > maxPasswordErrorNum) {
            log.info("当前错误次数{}, 最大次数:{}", passwordErrorNum, maxPasswordErrorNum);

            LocalDateTime passwordErrorLockTime = TimeUtils.getPasswordErrorLockTime("0");
            log.info("passwordErrorLockTime={}", passwordErrorLockTime);
            if (passwordErrorLockTime.isAfter(user.getPasswordErrorLastTime())) {
                // 登录失败事件
                String msg = StrUtil.format("密码连续输错次数已达到{}次,用户已被锁定~", maxPasswordErrorNum);
                SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
                return R.fail(msg);
            }
        }

        return R.success(user);
    }

    /**
     * 创建用户TOKEN
     *
     * @param user 用户
     * @return token
     */
    protected AuthInfo createToken(User user) {
        JwtUserInfo userInfo = new JwtUserInfo(user.getId(), user.getAccount(), user.getName());
        AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, null);
        authInfo.setAvatar(user.getAvatar());
        authInfo.setWorkDescribe(user.getWorkDescribe());
        return authInfo;
    }


}
