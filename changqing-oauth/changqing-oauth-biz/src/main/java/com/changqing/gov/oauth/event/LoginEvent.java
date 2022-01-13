package com.changqing.gov.oauth.event;

import com.changqing.gov.oauth.event.model.LoginStatusDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 *
 * @author changqing
 * @date 2020年03月18日17:22:55
 */
public class LoginEvent extends ApplicationEvent {
    public LoginEvent(LoginStatusDTO source) {
        super(source);
    }
}
