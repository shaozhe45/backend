package com.changqing.gov.sms.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.sms.dao.SmsSendStatusMapper;
import com.changqing.gov.sms.entity.SmsSendStatus;
import com.changqing.gov.sms.service.SmsSendStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 短信发送状态
 * </p>
 *
 * @author changqing
 * @date 2019-08-01
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class SmsSendStatusServiceImpl extends SuperServiceImpl<SmsSendStatusMapper, SmsSendStatus> implements SmsSendStatusService {

}
