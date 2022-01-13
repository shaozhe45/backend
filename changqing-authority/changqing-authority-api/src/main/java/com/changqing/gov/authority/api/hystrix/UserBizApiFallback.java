package com.changqing.gov.authority.api.hystrix;

import com.changqing.gov.authority.api.UserBizApi;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户API熔断
 *
 * @author changqing
 * @date 2019/07/23
 */
@Component
public class UserBizApiFallback implements UserBizApi {
    @Override
    public R<List<Long>> findAllUserId() {
        return R.timeout();
    }

    @Override
    public R<List<User>> findUserById(List<Long> ids) {
        return R.timeout();
    }
}
