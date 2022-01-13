package com.changqing.gov.oauth.service;

import java.util.Map;

import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.base.R;

/**
 * Created by zyongpei on 2020/12/10.
 */
public interface LdapService {
    void login(LoginParamDTO login);
}
