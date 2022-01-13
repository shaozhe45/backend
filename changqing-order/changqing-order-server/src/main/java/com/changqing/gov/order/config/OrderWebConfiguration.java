package com.changqing.gov.order.config;

import com.changqing.gov.oauth.api.LogApi;
import com.changqing.gov.boot.config.BaseConfig;
import com.changqing.gov.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author changqing
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class OrderWebConfiguration extends BaseConfig {

    @Bean
    @ConditionalOnExpression("${changqing.log.enabled:true} && 'DB'.equals('${changqing.log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogApi logApi) {
        return new SysLogListener((log) -> logApi.save(log));
    }
}
