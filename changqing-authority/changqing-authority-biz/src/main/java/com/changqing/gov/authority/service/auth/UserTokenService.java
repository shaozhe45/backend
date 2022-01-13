package com.changqing.gov.authority.service.auth;

import com.changqing.gov.authority.entity.auth.UserToken;
import com.changqing.gov.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * token
 * </p>
 *
 * @author changqing
 * @date 2020-04-02
 */
public interface UserTokenService extends SuperService<UserToken> {
    /**
     * 重置用户登录
     *
     * @return
     */
    boolean reset();
}
