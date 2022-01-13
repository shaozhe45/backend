package com.changqing.gov.oauth.config;

import com.changqing.gov.authority.service.common.OptLogService;
import com.changqing.gov.boot.config.BaseConfig;
import com.changqing.gov.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zuihou
 * @createTime 2017-12-15 14:42
 */
@Configuration
public class OauthWebConfiguration extends BaseConfig {

    /**
     * zuihou.log.enabled = true 并且 zuihou.log.type=DB时实例该类
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
