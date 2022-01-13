package com.changqing.gov.order.controller.injection;

import com.changqing.gov.base.R;
import com.changqing.gov.injection.core.InjectionCore;
import com.changqing.gov.order.entity.Order;
import com.changqing.gov.order.service.OrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author changqing
 * @date 2020/6/19 上午8:48
 */
@Slf4j
@RestController
@RequestMapping("/injection")
@Api(value = "injection", tags = "injection")
public class InjectionController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InjectionCore injectionCore;


    /**
     * education: 本地注入
     * org: 远程注入
     * nation: 远程注入
     *
     * @param data
     * @return
     */
    @PostMapping("/injection")
    public R injection(@RequestBody Order data) {
        List<Order> orders = orderService.find(data);
        injectionCore.injection(orders, false);
//        injectionCore.injection(orders);
        return R.success(orders);
    }

    @PostMapping("/autoInjection")
    public R autoInjection(@RequestBody Order data) {
        List<Order> orders = orderService.findInjectionResult(data);
        return R.success(orders);
    }


}
