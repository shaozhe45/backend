package com.changqing.gov.oauth.service.impl;

import static com.changqing.gov.utils.BizAssert.gt;
import static com.changqing.gov.utils.BizAssert.notNull;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.authority.entity.auth.Role;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.auth.UserRole;
import com.changqing.gov.authority.service.auth.RoleService;
import com.changqing.gov.authority.service.auth.UserRoleService;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.base.R;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.database.properties.MultiTenantType;
import com.changqing.gov.exception.code.ExceptionCode;
import com.changqing.gov.oauth.event.LoginEvent;
import com.changqing.gov.oauth.event.model.LoginStatusDTO;
import com.changqing.gov.oauth.granter.CaptchaTokenGranter;
import com.changqing.gov.oauth.service.LdapService;
import com.changqing.gov.oauth.utils.LdapAuthentication;
import com.changqing.gov.tenant.entity.Tenant;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.service.TenantService;
import com.changqing.gov.utils.BizAssert;
import com.changqing.gov.utils.SpringUtils;

/**
 * Created by zyongpei on 2020/12/10.
 */
@Service
public class LdapServiceImpl implements LdapService {

    @Autowired
    LdapAuthentication ldapAuthentication;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    protected DatabaseProperties databaseProperties;
    @Autowired
    protected TenantService tenantService;
    @Value("${role.name}")
    String roleName;

    @Override
    public void login(LoginParamDTO login) {
        String username = login.getAccount();
        String password = login.getPassword();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            if (ldapAuthentication.authenricate(username, password)) {

                login.setPassword("0");
                this.checkTenant(login.getTenant());
                User account = this.userService.getByAccount(username);
//                String passwordMd5 = cn.hutool.crypto.SecureUtil.md5(password);
                //用户是否已在平台注册
                if (account != null) {
                    return;
                }
                //保存用户信息
                User user = new User();
                user.setPassword(login.getPassword());
                user.setAccount(login.getAccount());
                user.setName(login.getAccount());
                user.setStatus(true);
                User saveUser = userService.saveUser(user);
                //授予用户默认角色
                UserRole userRole = new UserRole();
                userRole.setUserId(saveUser.getId());
                //                Role role = roleMapper.selectOne(Wraps.<Role>lbQ().eq(Role::getCode, INIT_ROLE_CODE));
                Role role = roleService.getOne(Wraps.<Role>lbQ().eq(Role::getName, roleName));
                userRole.setRoleId(role.getId());
                userRoleService.save(userRole);
            }
        }
    }
    public void checkTenant(String code){
        // 1，检测租户是否可用
        if (!MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType())) {
            Tenant tenant = this.tenantService.getByCode(code);
            notNull(tenant, "企业不存在");
            BizAssert.equals(TenantStatusEnum.NORMAL, tenant.getStatus(), "企业不可用~");
            if (tenant.getExpirationTime() != null) {
                gt(LocalDateTime.now(), tenant.getExpirationTime(), "企业服务已到期~");
            }
            BaseContextHandler.setTenant(tenant.getCode());
        }
    }
}
