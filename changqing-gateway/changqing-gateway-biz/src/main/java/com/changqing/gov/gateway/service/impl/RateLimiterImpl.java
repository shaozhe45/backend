package com.changqing.gov.gateway.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.common.constant.CacheKey;
import com.changqing.gov.gateway.dao.RateLimiterMapper;
import com.changqing.gov.gateway.entity.RateLimiter;
import com.changqing.gov.gateway.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 限流
 *
 * @author changqing
 * @date 2020/8/5 上午10:30
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class RateLimiterImpl extends SuperServiceImpl<RateLimiterMapper, RateLimiter> implements RateLimiterService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean save(RateLimiter model) {
        int bool = baseMapper.insert(model);

        saveRateLimiter(model);
        return SqlHelper.retBool(bool);
    }

    @Override
    public boolean updateById(RateLimiter model) {
        removeRateLimiter(model);
        int bool = baseMapper.updateById(model);
        saveRateLimiter(model);
        return SqlHelper.retBool(bool);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (idList.isEmpty()) {
            return true;
        }
        List<RateLimiter> rateLimiters = listByIds(idList);
        if (rateLimiters.isEmpty()) {
            return true;
        }
        baseMapper.deleteBatchIds(idList);
        rateLimiters.stream().forEach(this::removeRateLimiter);
        return true;
    }

    @Override
    public void saveRateLimiter(RateLimiter rateLimiter) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, rateLimiter.getRequestUri(), rateLimiter.getRequestMethod());
        redisTemplate.opsForValue().set(key, rateLimiter);
    }

    @Override
    public RateLimiter getRateLimiter(String uri, String method) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, uri, method);
        return (RateLimiter) redisTemplate.opsForValue().get(key);
    }


    @Override
    public void removeRateLimiter(RateLimiter rateLimiter) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, rateLimiter.getRequestUri(), rateLimiter.getRequestMethod());
        redisTemplate.delete(key);
    }

    @Override
    public int getCurrentRequestCount(String uri, String ip) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, uri, ip);
        return redisTemplate.hasKey(key) ? (int) redisTemplate.opsForValue().get(key) : 0;
    }


    @Override
    public void setCurrentRequestCount(String uri, String ip, Long time) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, uri, ip);
        time = time == null ? 0 : time;
        redisTemplate.opsForValue().set(key, 1, time, TimeUnit.SECONDS);
    }

    @Override
    public void incrCurrentRequestCount(String uri, String ip) {
        String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, uri, ip);
        redisTemplate.opsForValue().increment(key, 1L);
    }

    @Override
    public void loadAllRateLimiters() {
        List<RateLimiter> list = list();
        list.forEach((rateLimiter) -> {
            String key = CacheKey.buildTenantKey(CacheKey.RATE_LIMITER, rateLimiter.getRequestUri(), rateLimiter.getRequestMethod());
            redisTemplate.opsForValue().set(key, rateLimiter);
        });
    }
}
