package com.changqing.gov.order.api.fallback;

import com.changqing.gov.base.R;
import com.changqing.gov.order.api.DemoFeign2Api;
import com.changqing.gov.order.dto.RestTestDTO;
import org.springframework.stereotype.Component;

/**
 * @author changqing
 * @date 2020/6/10 下午10:46
 */
@Component
public class DemoFeign2ApiFallback implements DemoFeign2Api {
    @Override
    public R<RestTestDTO> fallback(String key) {
        return R.timeout();
    }
}
