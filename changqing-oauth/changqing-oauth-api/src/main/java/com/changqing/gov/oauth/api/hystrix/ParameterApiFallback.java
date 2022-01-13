package com.changqing.gov.oauth.api.hystrix;

import com.changqing.gov.base.R;
import com.changqing.gov.oauth.api.ParameterApi;
import org.springframework.stereotype.Component;


/**
 * 熔断类
 *
 * @author changqing
 * @date 2020年02月09日11:24:23
 */
@Component
public class ParameterApiFallback implements ParameterApi {
    @Override
    public R<String> getValue(String key, String defVal) {
        return R.timeout();
    }
}
