package com.changqing.gov.msgs.api;


import com.changqing.gov.base.R;
import com.changqing.gov.base.entity.SuperEntity;
import com.changqing.gov.msgs.api.fallback.SmsApiFallback;
import com.changqing.gov.sms.dto.SmsSendTaskDTO;
import com.changqing.gov.sms.dto.VerificationCodeDTO;
import com.changqing.gov.sms.entity.SmsTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 文件接口
 *
 * @author changqing
 * @date 2019/06/21
 */
@FeignClient(name = "${changqing.feign.msgs-server:changqing-msgs-server}", fallback = SmsApiFallback.class)
public interface SmsApi {
    /**
     * 短信发送
     *
     * @param smsTaskDTO
     * @return
     */
    @RequestMapping(value = "/smsTask/send", method = RequestMethod.POST)
    R<SmsTask> send(@RequestBody SmsSendTaskDTO smsTaskDTO);

    /**
     * 发送验证码
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/verification/send")
    R<Boolean> sendCode(@Validated @RequestBody VerificationCodeDTO data);

    /**
     * 验证
     *
     * @param data
     * @return
     */
    @PostMapping("/verification")
    R<Boolean> verification(@Validated(SuperEntity.Update.class) @RequestBody VerificationCodeDTO data);
}
