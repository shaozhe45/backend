package com.changqing.gov.gateway.config;

import com.changqing.gov.boot.config.BaseConfig;
import com.changqing.gov.common.properties.IgnoreTokenProperties;
import com.changqing.gov.gateway.filter.gateway.PreCheckFilter;
import com.changqing.gov.gateway.filter.zuul.ZuulMvcConfigurer;
import com.changqing.gov.gateway.filter.zuul.ZuulPreCheckFilter;
import com.changqing.gov.gateway.service.BlockListService;
import com.changqing.gov.gateway.service.RateLimiterService;
import com.changqing.gov.jwt.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author changqing
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class GatewayWebConfiguration extends BaseConfig {

    /**
     * changqing-zuul-server 服务在配置文件中配置 changqing.webmvc.enabled=true ，会加载下面2个Bean
     */
    @ConditionalOnProperty(prefix = "changqing.webmvc", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class WebmvcConfig {
        /**
         * zuul服务 限流 + 阻止访问 功能的过滤器
         *
         * @param blockListService   阻止列表Service （spring自动注入）
         * @param rateLimiterService 限流Service （spring自动注入）
         * @return
         */
        @Bean
        public ZuulPreCheckFilter getZuulPreCheckFilter(BlockListService blockListService, RateLimiterService rateLimiterService) {
            return new ZuulPreCheckFilter(blockListService, rateLimiterService);
        }

        @Bean
        public ZuulMvcConfigurer getZuulMvcConfigurer(TokenUtil tokenUtil, @Value("${changqing.database.multiTenantType:SCHEMA}") String multiTenantType,
                                                      IgnoreTokenProperties ignoreTokenProperties) {
            return new ZuulMvcConfigurer(tokenUtil, multiTenantType, ignoreTokenProperties);
        }

    }

    /**
     * changqing-gateway-server 服务在配置文件中配置 changqing.webmvc.enabled=false ，会加载下面1个Bean
     */
    @ConditionalOnProperty(prefix = "changqing.webmvc", name = "enabled", havingValue = "false", matchIfMissing = true)
    public static class WebfluxConfig {
        /**
         * gateway服务 限流 + 阻止访问 功能的过滤器
         *
         * @param blockListService   阻止列表Service （spring自动注入）
         * @param rateLimiterService 限流Service （spring自动注入）
         * @return
         */
        @Bean
        public PreCheckFilter getPreCheckFilter(BlockListService blockListService, RateLimiterService rateLimiterService) {
            return new PreCheckFilter(blockListService, rateLimiterService);
        }
    }
}
