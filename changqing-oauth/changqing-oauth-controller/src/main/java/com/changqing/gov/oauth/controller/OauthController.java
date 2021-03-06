package com.changqing.gov.oauth.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changqing.gov.authority.dto.auth.LoginParamDTO;
import com.changqing.gov.base.R;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.jwt.TokenUtil;
import com.changqing.gov.jwt.model.AuthInfo;
import com.changqing.gov.jwt.utils.JwtUtil;
import com.changqing.gov.oauth.granter.TokenGranter;
import com.changqing.gov.oauth.granter.TokenGranterBuilder;
import com.changqing.gov.oauth.service.AdminUiService;
import com.changqing.gov.oauth.service.LdapService;
import com.changqing.gov.oauth.service.ValidateCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证Controller
 *
 * @author changqing
 * @date 2020年03月31日10:10:36
 */
@Slf4j
@RestController
@RequestMapping("/anno")
@AllArgsConstructor
@Api(value = "用户授权认证", tags = "登录接口")
public class OauthController {

    private final ValidateCodeService validateCodeService;
    private final TokenGranterBuilder tokenGranterBuilder;
    private final AdminUiService authManager;
    private final TokenUtil tokenUtil;

    @Autowired
    LdapService ldapService;
    /**
     * 租户登录 changqing-ui 系统
     *
     * @param login
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取认证token（登录接口）", notes = "登录或者清空缓存时调用")
    @PostMapping(value = "/token")
    public R<AuthInfo> login(@Validated @RequestBody LoginParamDTO login) throws BizException {

        log.info("61param="+JSONObject.toJSONString(login));

        if (StrUtil.isEmpty(login.getTenant())) {
            login.setTenant(BaseContextHandler.getTenant());
        }
        login.setTenant(JwtUtil.base64Decoder(login.getTenant()));

        TokenGranter granter = tokenGranterBuilder.getGranter(login.getGrantType());
        R<AuthInfo> userInfo = granter.grant(login);

        return userInfo;
    }

    /**
     * 租户登录 changqing-ui 系统
     *
     * @param login
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取认证token（ldap登录接口）", notes = "登录或者清空缓存时调用")
    @PostMapping(value = "/ldap")
    public R<AuthInfo> ldap(@Validated @RequestBody LoginParamDTO login) throws BizException {

        log.info("61param="+JSONObject.toJSONString(login));

        if (StrUtil.isEmpty(login.getTenant())) {
            login.setTenant(BaseContextHandler.getTenant());
        }
        login.setTenant(JwtUtil.base64Decoder(login.getTenant()));
        TokenGranter granter = tokenGranterBuilder.getGranter(login.getGrantType());
        ldapService.login(login);
        R<AuthInfo> userInfo = granter.grant(login);

        return userInfo;
    }
    /**
     * 验证验证码
     *
     * @param key  验证码唯一uuid key
     * @param code 验证码
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "验证验证码", notes = "验证验证码")
    @GetMapping(value = "/check")
    public R<Boolean> check(@RequestParam(value = "key") String key, @RequestParam(value = "code") String code) throws BizException {
        return this.validateCodeService.check(key, code);
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @GetMapping(value = "/captcha", produces = "image/png")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) throws IOException {
        this.validateCodeService.create(key, response);
    }

    /**
     * 验证token
     *
     * @param token
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "验证token", notes = "验证token")
    @GetMapping(value = "/verify")
    public R<AuthInfo> verify(@RequestParam(value = "token") String token) throws BizException {
        return R.success(tokenUtil.getAuthInfo(token));
    }


    /**
     * 管理员登录 changqing-admin-ui 系统
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "超级管理员登录", notes = "超级管理员登录")
    @PostMapping(value = "/admin/login")
    public R<AuthInfo> loginAdminTx(@Validated @RequestBody LoginParamDTO login) throws BizException {
        log.info("account={}", login.getAccount());
        R<Boolean> check = this.validateCodeService.check(login.getKey(), login.getCode());
        if (check.getIsError()) {
            return R.fail(check.getMsg());
        }
        return authManager.adminLogin(login.getAccount(), login.getPassword());
    }
}
