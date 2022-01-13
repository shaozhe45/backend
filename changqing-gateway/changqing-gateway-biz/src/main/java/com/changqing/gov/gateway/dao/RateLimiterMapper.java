package com.changqing.gov.gateway.dao;

import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.gateway.entity.RateLimiter;
import org.springframework.stereotype.Repository;

/**
 * 限流
 *
 * @author changqing
 * @date 2020/8/5 上午10:31
 */
@Repository
public interface RateLimiterMapper extends SuperMapper<RateLimiter> {
}
