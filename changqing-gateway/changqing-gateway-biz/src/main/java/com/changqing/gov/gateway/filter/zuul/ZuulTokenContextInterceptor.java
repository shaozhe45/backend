package com.changqing.gov.gateway.filter.zuul;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.changqing.gov.common.properties.IgnoreTokenProperties;
import com.changqing.gov.context.BaseContextConstants;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.properties.MultiTenantType;
import com.changqing.gov.jwt.TokenUtil;
import com.changqing.gov.jwt.model.AuthInfo;
import com.changqing.gov.jwt.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.changqing.gov.context.BaseContextConstants.BASIC_HEADER_KEY;
import static com.changqing.gov.context.BaseContextConstants.BEARER_HEADER_KEY;
import static com.changqing.gov.context.BaseContextConstants.JWT_KEY_TENANT;

/**
 * 解决zuul服务的Controller请求，无法先通过zuul的过滤器（TokenContextFilter）解析租户编码和用户token的问题
 * <p>
 * 这个拦截器会拦截zuul 服务中所有的Controller，实现的功能跟 （TokenContextFilter） 基本一致
 *
 * @author changqing
 * @date 2019-06-20 22:22
 */
@Slf4j
@AllArgsConstructor
public class ZuulTokenContextInterceptor extends HandlerInterceptorAdapter {

    private final TokenUtil tokenUtil;
    private final String multiTenantType;
    protected final IgnoreTokenProperties ignoreTokenProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        //1, 解码 请求头中的租户信息
        if (!MultiTenantType.NONE.name().equals(multiTenantType)) {
            String base64Tenant = getHeader(JWT_KEY_TENANT, request);
            if (StrUtil.isNotEmpty(base64Tenant)) {
                String tenant = JwtUtil.base64Decoder(base64Tenant);
                BaseContextHandler.setTenant(tenant);
                MDC.put(BaseContextConstants.JWT_KEY_TENANT, BaseContextHandler.getTenant());
            }
        }

        // 2,解码 Authorization 后面完善
        String base64Authorization = getHeader(BASIC_HEADER_KEY, request);
        if (StrUtil.isNotEmpty(base64Authorization)) {
            String[] client = JwtUtil.getClient(base64Authorization);
            BaseContextHandler.setClientId(client[0]);
        }

        // 忽略 token 认证的接口
        if (isIgnoreToken(request.getRequestURI())) {
            log.debug("access filter not execute");
            return super.preHandle(request, response, handler);
        }

        //获取token， 解析，然后想信息放入 heade
        //3, 获取token
        String token = getHeader(BEARER_HEADER_KEY, request);

        // 4, 解析 并 验证 token
        AuthInfo authInfo = tokenUtil.getAuthInfo(token);

        //6, 转换，将 token 解析出来的用户身份 和 解码后的tenant、Authorization 重新封装到请求头
        if (authInfo != null) {
            BaseContextHandler.setUserId(authInfo.getUserId());
            BaseContextHandler.setName(authInfo.getName());
            BaseContextHandler.setAccount(authInfo.getAccount());
            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(authInfo.getUserId()));
        }

        return super.preHandle(request, response, handler);
    }

    /**
     * 忽略应用级token
     *
     * @return
     */
    protected boolean isIgnoreToken(String uri) {
        return ignoreTokenProperties.isIgnoreToken(uri);
    }

    protected String getHeader(String headerName, HttpServletRequest request) {
        String token = request.getHeader(headerName);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(headerName);
        }
        return token;
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return URLUtil.decode(value);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }

}
