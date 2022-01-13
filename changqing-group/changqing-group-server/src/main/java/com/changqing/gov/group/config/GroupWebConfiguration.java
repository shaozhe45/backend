package com.changqing.gov.group.config;

import com.changqing.gov.boot.config.BaseConfig;
import com.changqing.gov.log.event.SysLogListener;
import com.changqing.gov.oauth.api.LogApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 环保标准化管理平台-Web配置
 *
 * @author tanbao
 * @date 2021-08-24
 */
@Configuration
public class GroupWebConfiguration extends BaseConfig {

    /**
    * zuihou.log.enabled = true 并且 zuihou.log.type=DB时实例该类
    *
    * @param logApi
    * @return
    */
    @Bean
    @ConditionalOnExpression("${zuihou.log.enabled:true} && 'DB'.equals('${zuihou.log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogApi logApi) {
        return new SysLogListener((log) -> logApi.save(log));
    }
}
