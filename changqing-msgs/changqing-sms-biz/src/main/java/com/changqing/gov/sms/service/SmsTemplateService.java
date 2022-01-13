package com.changqing.gov.sms.service;

import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.sms.entity.SmsTemplate;

/**
 * <p>
 * 业务接口
 * 短信模板
 * </p>
 *
 * @author changqing
 * @date 2019-08-01
 */
public interface SmsTemplateService extends SuperService<SmsTemplate> {
    /**
     * 保存模板，并且将模板内容解析成json格式
     *
     * @param smsTemplate
     * @return
     * @author changqing
     * @date 2019-05-16 21:13
     */
    void saveTemplate(SmsTemplate smsTemplate);

    /**
     * 修改
     *
     * @param smsTemplate
     */
    void updateTemplate(SmsTemplate smsTemplate);
}
