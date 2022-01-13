package com.changqing.gov.oauth.granter;

import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.base.R;
import com.changqing.gov.jwt.model.AuthInfo;
import org.springframework.stereotype.Component;

import static com.changqing.gov.oauth.granter.PasswordTokenGranter.GRANT_TYPE;

/**
 * 账号密码登录获取token
 *
 * @author changqing
 * @date 2020年03月31日10:22:55
 */
@Component(GRANT_TYPE)
public class PasswordTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "password";

    @Override
    public R<AuthInfo> grant(LoginParamDTO tokenParameter) {
        return login(tokenParameter);
    }
}
