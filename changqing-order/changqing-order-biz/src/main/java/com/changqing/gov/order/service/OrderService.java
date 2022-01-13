package com.changqing.gov.order.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.service.SuperCacheService;
import com.changqing.gov.order.entity.Order;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 订单
 * </p>
 *
 * @author changqing
 * @date 2019-08-13
 */
public interface OrderService extends SuperCacheService<Order> {

    List<Order> find(Order data);

    List<Order> findInjectionResult(Order data);

    IPage<Order> findPage(IPage page, Wrapper<Order> wrapper);
}
