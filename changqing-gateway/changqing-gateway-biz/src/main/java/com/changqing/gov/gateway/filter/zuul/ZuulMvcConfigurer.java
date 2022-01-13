package com.changqing.gov.gateway.filter.zuul;

import com.changqing.gov.common.properties.IgnoreTokenProperties;
import com.changqing.gov.jwt.TokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 公共配置类, 一些公共工具配置
 *
 * @author changqing
 * @date 2018/8/25
 */
@AllArgsConstructor
public class ZuulMvcConfigurer implements WebMvcConfigurer {

    private final TokenUtil tokenUtil;
    private final String multiTenantType;
    private final IgnoreTokenProperties ignoreTokenProperties;
    /**
     * 注册 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ZuulTokenContextInterceptor(tokenUtil, multiTenantType, ignoreTokenProperties))
                .addPathPatterns("/**")
                .order(1);
    }
}
