package com.changqing.gov.group.config.mq;

import com.alibaba.fastjson.JSONObject;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.mq.properties.MqProperties;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import com.changqing.gov.tenant.service.DataSourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import static com.changqing.gov.common.constant.BizConstant.*;

/**
 * 消息队列开启时，启用
 *
 * @author tanbao
 * @date 2021-08-24
 */
@Slf4j
@AllArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = MqProperties.PREFIX, name = "enabled", havingValue = "true")
public class GroupTenantDatasourceConfiguration {
    /** 建议将该变量手动移动到： BizMqQueue 类 */
    private final static String TENANT_DS_QUEUE_BY_GROUP = "tenant_ds_group";
    private final DataSourceService dataSourceService;

    @Bean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "multiTenantType", havingValue = "DATASOURCE")
    public Queue dsQueue() {
        return new Queue(TENANT_DS_QUEUE_BY_GROUP);
    }

//    @Bean
//    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "multiTenantType", havingValue = "DATASOURCE")
//    public Binding dsQueueBinding() {
//        return new Binding(TENANT_DS_QUEUE_BY_GROUP, Binding.DestinationType.QUEUE, BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_GROUP, "", new HashMap(1));
//    }

    @RabbitListener(queues = TENANT_DS_QUEUE_BY_GROUP)
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "multiTenantType", havingValue = "DATASOURCE")
    public void dsRabbitListener(@Payload String param) {
        log.debug("异步初始化数据源=={}", param);
        JSONObject map = JSONObject.parseObject(param);
        if (INIT_DS_PARAM_METHOD_INIT.equals(map.getString(INIT_DS_PARAM_METHOD))) {
            dataSourceService.initConnect(map.getObject(INIT_DS_PARAM_TENANT, DataSourcePropertyDTO.class));
        } else {
            dataSourceService.remove(map.getString(INIT_DS_PARAM_TENANT));
        }
    }
}
