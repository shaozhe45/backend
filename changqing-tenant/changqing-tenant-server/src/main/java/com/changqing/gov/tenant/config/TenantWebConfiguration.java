package com.changqing.gov.tenant.config;

import com.changqing.gov.authority.service.common.OptLogService;
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
public class TenantWebConfiguration extends BaseConfig {

    /**
     * changqing.log.enabled = true 并且 changqing.log.type=DB时实例该类
     *
     * @param optLogService
     * @return
     */
    @Bean
    @ConditionalOnExpression("${changqing.log.enabled:true} && 'DB'.equals('${changqing.log.type:LOGGER}')")
    public SysLogListener sysLogListener(OptLogService optLogService) {
        return new SysLogListener((log) -> optLogService.save(log));
    }
}
