package com.changqing.gov.oauth.granter;


import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.base.R;
import com.changqing.gov.jwt.model.AuthInfo;

/**
 * 授权认证统一接口.
 *
 * @author changqing
 * @date 2020年03月31日10:21:21
 */
public interface TokenGranter {

    /**
     * 获取用户信息
     *
     * @param loginParam 授权参数
     * @return LoginDTO
     */
    R<AuthInfo> grant(LoginParamDTO loginParam);

}
