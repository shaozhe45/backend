package com.changqing.gov.sms.strategy;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.R;
import com.changqing.gov.sms.dao.SmsTaskMapper;
import com.changqing.gov.sms.dao.SmsTemplateMapper;
import com.changqing.gov.sms.entity.SmsTask;
import com.changqing.gov.sms.entity.SmsTemplate;
import com.changqing.gov.utils.BizAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信发送上下文
 *
 * @author changqing
 * @date 2019-05-15
 */
@Component
@DS("#thread.tenant")
public class SmsContext {
    private final Map<String, SmsStrategy> smsContextStrategyMap = new ConcurrentHashMap<>();

    private final SmsTaskMapper smsTaskMapper;
    private final SmsTemplateMapper smsTemplateMapper;

    @Autowired
    public SmsContext(
            Map<String, SmsStrategy> strategyMap,
            SmsTaskMapper smsTaskMapper,
            SmsTemplateMapper smsTemplateMapper) {
        strategyMap.forEach(this.smsContextStrategyMap::put);
        this.smsTaskMapper = smsTaskMapper;
        this.smsTemplateMapper = smsTemplateMapper;
    }

    /**
     * 根据任务id发送短信
     * <p>
     * 待完善的点：
     * 1， 查询次数过多，想办法优化
     *
     * @param taskId
     * @return
     */
    public String smsSend(Long taskId) {
        SmsTask smsTask = smsTaskMapper.selectById(taskId);
        BizAssert.notNull(smsTask, "短信任务尚未保存成功");

        SmsTemplate template = smsTemplateMapper.selectById(smsTask.getTemplateId());
        BizAssert.notNull(template, "短信模板为空");

        // 根据短信任务选择的服务商，动态选择短信服务商策略类来具体发送短信
        SmsStrategy smsStrategy = smsContextStrategyMap.get(template.getProviderType().name());
        BizAssert.notNull(smsStrategy, "短信供应商不存在");

        R<String> result = smsStrategy.sendSms(smsTask, template);
        if (result.getIsSuccess()) {
            return result.getData();
        }
        return null;
    }

}
