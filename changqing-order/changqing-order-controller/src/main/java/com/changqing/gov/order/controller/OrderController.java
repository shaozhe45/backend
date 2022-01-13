package com.changqing.gov.order.controller;


import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.order.dto.OrderPageDTO;
import com.changqing.gov.order.dto.OrderSaveDTO;
import com.changqing.gov.order.dto.OrderUpdateDTO;
import com.changqing.gov.order.entity.Order;
import com.changqing.gov.order.service.OrderService;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 订单
 * </p>
 *
 * @author changqing
 * @date 2019-08-13
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/order")
@Api(value = "Order", tags = "订单")
@PreAuth(replace = "order:", enabled = false)
public class OrderController extends SuperCacheController<OrderService, Long, Order, OrderPageDTO, OrderSaveDTO, OrderUpdateDTO> {

}
