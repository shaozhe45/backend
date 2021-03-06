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
 * ?????????TokenGranter
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
     * ??????????????????
     *
     * @param loginParam ????????????
     * @return ????????????
     */
    protected R<AuthInfo> login(LoginParamDTO loginParam) {
        if (StrHelper.isAnyBlank(loginParam.getAccount(), loginParam.getPassword())) {
            return R.fail("???????????????????????????");
        }
        // 1???????????????????????????
        if (!MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType())) {
            Tenant tenant = this.tenantService.getByCode(loginParam.getTenant());
            notNull(tenant, "???????????????");
            BizAssert.equals(TenantStatusEnum.NORMAL, tenant.getStatus(), "???????????????~");
            if (tenant.getExpirationTime() != null) {
                gt(LocalDateTime.now(), tenant.getExpirationTime(), "?????????????????????~");
            }
            BaseContextHandler.setTenant(tenant.getCode());
        }

        // 2.??????client????????????
        R<String[]> checkR = checkClient();
        if (checkR.getIsError()) {
            return R.fail(checkR.getMsg());
        }

        // 3. ????????????
        R<User> result = this.getUser(loginParam.getAccount(), loginParam.getPassword());
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }

        // 4.?????????????????????
        User user = result.getData();

        // 5.?????? token
        AuthInfo authInfo = this.createToken(user);

        UserToken userToken = getUserToken(checkR.getData()[0], authInfo);

        //??????????????????
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
     * ?????? client
     *
     * @return
     */
    protected R<String[]> checkClient() {
        String basicHeader = ServletUtil.getHeader(WebUtils.request(), BASIC_HEADER_KEY, StrPool.UTF_8);
        String[] client = JwtUtil.getClient(basicHeader);
        Application application = applicationService.getByClient(client[0], client[1]);

        if (application == null) {
            return R.fail("???????????????????????????ID?????????????????????");
        }
        if (!application.getStatus()) {
            return R.fail("?????????[%s]????????????", application.getClientId());
        }
        return R.success(client);
    }


    /**
     * ??????????????????????????????
     *
     * @param account  ??????
     * @param password ??????
     * @return ????????????
     */
    protected R<User> getUser(String account, String password) {
        User user = this.userService.getByAccount(account);
        // ????????????
        String passwordMd5 = cn.hutool.crypto.SecureUtil.md5(password);
        if (user == null) {
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }

        System.out.println("password="+user.getPassword());

        if (!user.getPassword().equalsIgnoreCase(passwordMd5)) {
            String msg = "????????????????????????!";
            // ??????????????????
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.pwdError(user.getId(), msg)));
            return R.fail(msg);
        }

        // ????????????
        if (user.getPasswordExpireTime() != null && LocalDateTime.now().isAfter(user.getPasswordExpireTime())) {
            String msg = "??????????????????????????????????????????????????????????????????!";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        if (!user.getStatus()) {
            String msg = "???????????????????????????????????????";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }
        System.out.println(user.toString());
        if (user.getIsDelete()!=null&&user.getIsDelete()) {
            String msg = "???????????????????????????????????????";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
            return R.fail(msg);
        }

        // ????????????
        Integer maxPasswordErrorNum = 0;
        Integer passwordErrorNum = Convert.toInt(user.getPasswordErrorNum(), 0);
        if (maxPasswordErrorNum > 0 && passwordErrorNum > maxPasswordErrorNum) {
            log.info("??????????????????{}, ????????????:{}", passwordErrorNum, maxPasswordErrorNum);

            LocalDateTime passwordErrorLockTime = TimeUtils.getPasswordErrorLockTime("0");
            log.info("passwordErrorLockTime={}", passwordErrorLockTime);
            if (passwordErrorLockTime.isAfter(user.getPasswordErrorLastTime())) {
                // ??????????????????
                String msg = StrUtil.format("?????????????????????????????????{}???,??????????????????~", maxPasswordErrorNum);
                SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), msg)));
                return R.fail(msg);
            }
        }

        return R.success(user);
    }

    /**
     * ????????????TOKEN
     *
     * @param user ??????
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
