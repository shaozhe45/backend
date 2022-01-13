package com.changqing.gov.msgs.api.fallback;

import com.changqing.gov.base.R;
import com.changqing.gov.msgs.api.SmsApi;
import com.changqing.gov.sms.dto.SmsSendTaskDTO;
import com.changqing.gov.sms.dto.VerificationCodeDTO;
import com.changqing.gov.sms.entity.SmsTask;
import org.springframework.stereotype.Component;

/**
 * 熔断
 *
 * @author changqing
 * @date 2019/07/25
 */
@Component
public class SmsApiFallback implements SmsApi {
    @Override
    public R<SmsTask> send(SmsSendTaskDTO smsTaskDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> sendCode(VerificationCodeDTO data) {
        return R.timeout();
    }

    @Override
    public R<Boolean> verification(VerificationCodeDTO data) {
        return R.timeout();
    }
}
