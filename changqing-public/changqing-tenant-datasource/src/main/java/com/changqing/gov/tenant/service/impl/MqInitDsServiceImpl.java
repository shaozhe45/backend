package com.changqing.gov.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.common.constant.BizMqQueue;
import com.changqing.gov.mq.properties.MqProperties;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import com.changqing.gov.tenant.service.InitDsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.changqing.gov.common.constant.BizConstant.INIT_DS_PARAM_METHOD;
import static com.changqing.gov.common.constant.BizConstant.INIT_DS_PARAM_METHOD_INIT;
import static com.changqing.gov.common.constant.BizConstant.INIT_DS_PARAM_METHOD_REMOVE;
import static com.changqing.gov.common.constant.BizConstant.INIT_DS_PARAM_TENANT;


/**
 * 开启消息队列就广播
 *
 * @author changqing
 * @date 2020年04月05日16:27:03
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = MqProperties.PREFIX, name = "enabled", havingValue = "true")
public class MqInitDsServiceImpl implements InitDsService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public FanoutExchange getFanoutExchangeAuthority() {
        FanoutExchange queue = new FanoutExchange(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_AUTHORITY);
        log.debug("Query {} [{}]", BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_AUTHORITY, queue);
        return queue;
    }

    @Bean
    public FanoutExchange getFanoutExchangeFile() {
        FanoutExchange queue = new FanoutExchange(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_FILE);
        log.debug("Query {} [{}]", BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_FILE, queue);
        return queue;
    }

    @Bean
    public FanoutExchange getFanoutExchangeMsgs() {
        FanoutExchange queue = new FanoutExchange(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_MSGS);
        log.debug("Query {} [{}]", BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_MSGS, queue);
        return queue;
    }

    @Bean
    public FanoutExchange getFanoutExchangeGateway() {
        FanoutExchange queue = new FanoutExchange(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_GATEWAY);
        log.debug("Query {} [{}]", BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_GATEWAY, queue);
        return queue;
    }

    @Bean
    public FanoutExchange getFanoutExchangeOauth() {
        FanoutExchange queue = new FanoutExchange(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_OAUTH);
        log.debug("Query {} [{}]", BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_AUTHORITY, queue);
        return queue;
    }

    @Override
    public boolean initConnect(Map<String, DatasourceConfig> typeMap) {
        DataSourcePropertyDTO authorityDsp = BeanUtil.toBean(typeMap.get(BizConstant.AUTHORITY), DataSourcePropertyDTO.class);
        JSONObject param = new JSONObject();
        param.put(INIT_DS_PARAM_TENANT, authorityDsp);
        param.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_INIT);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_AUTHORITY, null, param.toString());

//         oauth
        JSONObject oauth = new JSONObject();
        oauth.put(INIT_DS_PARAM_TENANT, BeanUtil.toBean(typeMap.get(BizConstant.OAUTH), DataSourcePropertyDTO.class));
        oauth.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_INIT);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_OAUTH, null, oauth.toString());

        // file
        JSONObject file = new JSONObject();
        file.put(INIT_DS_PARAM_TENANT, BeanUtil.toBean(typeMap.get(BizConstant.FILE), DataSourcePropertyDTO.class));
        file.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_INIT);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_FILE, null, file.toString());


        // msgs
        JSONObject msgs = new JSONObject();
        msgs.put(INIT_DS_PARAM_TENANT, BeanUtil.toBean(typeMap.get(BizConstant.MSGS), DataSourcePropertyDTO.class));
        msgs.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_INIT);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_MSGS, null, msgs.toString());

        // 网关
        JSONObject gate = new JSONObject();
        gate.put(INIT_DS_PARAM_TENANT, BeanUtil.toBean(typeMap.get(BizConstant.GATE), DataSourcePropertyDTO.class));
        gate.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_INIT);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_GATEWAY, null, gate.toString());

        return true;
    }

    @Override
    public boolean removeDataSource(String tenant) {
        JSONObject param = new JSONObject();
        param.put(INIT_DS_PARAM_TENANT, tenant);
        param.put(INIT_DS_PARAM_METHOD, INIT_DS_PARAM_METHOD_REMOVE);
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_GATEWAY, null, param.toString());
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_AUTHORITY, null, param.toString());
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_FILE, null, param.toString());
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_OAUTH, null, param.toString());
        rabbitTemplate.convertAndSend(BizMqQueue.TENANT_DS_FANOUT_EXCHANGE_MSGS, null, param.toString());
        return true;
    }
}
