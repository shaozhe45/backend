package com.changqing.gov.file.config;

import com.changqing.gov.boot.config.BaseConfig;
import com.changqing.gov.log.event.SysLogListener;
import com.changqing.gov.oauth.api.LogApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author changqing
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class FileWebConfiguration extends BaseConfig {
    /**
     * changqing.log.enabled = true 并且 changqing.log.type=DB时实例该类
     *
     * @param logApi
     * @return
     */
    @Bean
    @ConditionalOnExpression("${changqing.log.enabled:true} && 'DB'.equals('${changqing.log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogApi logApi) {
        return new SysLogListener((log) -> logApi.save(log));
    }
}
