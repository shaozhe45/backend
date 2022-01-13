package com.changqing.gov.oauth.api.hystrix;

import com.changqing.gov.oauth.api.RoleApi;
import com.changqing.gov.base.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色查询API
 *
 * @author changqing
 * @date 2019/08/02
 */
@Component
public class RoleApiFallback implements RoleApi {
    @Override
    public R<List<Long>> findUserIdByCode(String[] codes) {
        return R.timeout();
    }
}
