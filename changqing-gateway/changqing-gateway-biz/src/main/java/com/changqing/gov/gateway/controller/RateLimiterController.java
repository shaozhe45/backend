package com.changqing.gov.gateway.controller;

import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.gateway.entity.RateLimiter;
import com.changqing.gov.gateway.service.RateLimiterService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 限流
 * <p>
 * 本来想打造一个zuul 和 gateway 都能共用的限流和阻止列表功能，但由于2者机制不同，zuul服务在使用该功能时，需要自行调整以下2个地方：
 * 1，changqing-ui/src/api/BlockList.js 所有的url增加 /gate 的前缀。 如： /gate/gateway/blocklist/page
 * 2，changqing-ui/src/api/RateLimiter.js 所有的url增加 /gate 的前缀。 如： /gate/gateway/rateLimiter/page
 *
 * @author changqing
 * @date 2020/8/4 上午8:59
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/gateway/rateLimiter")
@Api(value = "RateLimiter", tags = "限流")
public class RateLimiterController extends SuperController<RateLimiterService, Long, RateLimiter, RateLimiter, RateLimiter, RateLimiter> {

}
