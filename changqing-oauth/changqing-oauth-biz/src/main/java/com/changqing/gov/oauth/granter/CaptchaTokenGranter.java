package com.changqing.gov.oauth.granter;

import com.alibaba.fastjson.JSONObject;
import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.base.R;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.jwt.model.AuthInfo;
import com.changqing.gov.oauth.event.LoginEvent;
import com.changqing.gov.oauth.event.model.LoginStatusDTO;
import com.changqing.gov.oauth.service.ValidateCodeService;
import com.changqing.gov.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.changqing.gov.oauth.granter.CaptchaTokenGranter.GRANT_TYPE;

/**
 * 验证码TokenGranter
 *
 * @author Chill
 */
@Component(GRANT_TYPE)
@Slf4j
public class CaptchaTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "captcha";
    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    public R<AuthInfo> grant(LoginParamDTO loginParam) {
        log.info("34param="+ JSONObject.toJSONString(loginParam));
        R<Boolean> check = validateCodeService.check(loginParam.getKey(), loginParam.getCode());
        log.info("36param="+ JSONObject.toJSONString(check));
        if (check.getIsError()) {
            String msg = check.getMsg();
            BaseContextHandler.setTenant(loginParam.getTenant());
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(loginParam.getAccount(), msg)));
            throw BizException.validFail(check.getMsg());
        }

        return login(loginParam);
    }

}
