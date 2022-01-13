package com.changqing.gov.order.api;

import com.changqing.gov.base.R;
import com.changqing.gov.order.api.fallback.DemoFeign2ApiFallback;
import com.changqing.gov.order.dto.RestTestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 测试日期类型API接口
 *
 * @author changqing
 * @date 2019/07/24
 */
@FeignClient(name = "${changqing.feign.demo-server:changqing-demo-server}", path = "/restTemplate", fallback = DemoFeign2ApiFallback.class)
public interface DemoFeign2Api {

    @PostMapping("/fallback")
    R<RestTestDTO> fallback(@RequestParam("key") String key);

}
