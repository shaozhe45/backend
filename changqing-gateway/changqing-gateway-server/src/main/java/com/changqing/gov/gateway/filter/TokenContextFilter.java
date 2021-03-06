package com.changqing.gov.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.changqing.gov.base.R;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.common.constant.CacheKey;
import com.changqing.gov.common.properties.IgnoreTokenProperties;
import com.changqing.gov.context.BaseContextConstants;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.properties.MultiTenantType;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.jwt.TokenUtil;
import com.changqing.gov.jwt.model.AuthInfo;
import com.changqing.gov.jwt.utils.JwtUtil;
import com.changqing.gov.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.changqing.gov.context.BaseContextConstants.BASIC_HEADER_KEY;
import static com.changqing.gov.context.BaseContextConstants.BEARER_HEADER_KEY;
import static com.changqing.gov.context.BaseContextConstants.JWT_KEY_CLIENT_ID;
import static com.changqing.gov.context.BaseContextConstants.JWT_KEY_TENANT;
import static com.changqing.gov.exception.code.ExceptionCode.JWT_OFFLINE;

/**
 * ?????????
 *
 * @author changqing
 * @date 2019/07/31
 */
@Component
@Slf4j
@EnableConfigurationProperties({IgnoreTokenProperties.class})
public class TokenContextFilter implements WebFilter, Ordered {
    @Value("${spring.profiles.active:dev}")
    protected String profiles;
    @Value("${changqing.database.multiTenantType:SCHEMA}")
    protected String multiTenantType;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private IgnoreTokenProperties ignoreTokenProperties;


    @Autowired
    private CacheChannel channel;

    protected boolean isDev(String token) {
        return !StrPool.PROD.equalsIgnoreCase(profiles) && (StrPool.TEST_TOKEN.equalsIgnoreCase(token) || StrPool.TEST.equalsIgnoreCase(token));
    }

    @Override
    public int getOrder() {
        return -1000;
    }


    /**
     * ???????????????token
     *
     * @return
     */
    protected boolean isIgnoreToken(String path) {
        return ignoreTokenProperties.isIgnoreToken(path);
    }

    protected String getHeader(String headerName, ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String token = StrUtil.EMPTY;
        if (headers == null || headers.isEmpty()) {
            return token;
        }

        token = headers.getFirst(headerName);

        if (StringUtils.isNotBlank(token)) {
            return token;
        }

        return request.getQueryParams().getFirst(headerName);
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();

        BaseContextHandler.setGrayVersion(getHeader(BaseContextConstants.GRAY_VERSION, request));

        AuthInfo authInfo = null;
        try {
            //1, ?????? ???????????????????????????
            if (!MultiTenantType.NONE.name().equals(multiTenantType)) {
                String base64Tenant = getHeader(JWT_KEY_TENANT, request);
                if (StrUtil.isNotEmpty(base64Tenant)) {
                    String tenant = JwtUtil.base64Decoder(base64Tenant);
                    BaseContextHandler.setTenant(tenant);
                    addHeader(mutate, JWT_KEY_TENANT, BaseContextHandler.getTenant());
                    MDC.put(JWT_KEY_TENANT, BaseContextHandler.getTenant());
                }
            }

            // 2,?????? Authorization ????????????
            String base64Authorization = getHeader(BASIC_HEADER_KEY, request);
            if (StrUtil.isNotEmpty(base64Authorization)) {
                String[] client = JwtUtil.getClient(base64Authorization);
                BaseContextHandler.setClientId(client[0]);
                addHeader(mutate, JWT_KEY_CLIENT_ID, BaseContextHandler.getClientId());
            }

            // ?????? token ???????????????
            if (isIgnoreToken(request.getPath().toString())) {
                log.debug("access filter not execute");
                return chain.filter(exchange);
            }

            //??????token??? ?????????????????????????????? heade
            //3, ??????token
            String token = getHeader(BEARER_HEADER_KEY, request);

            // ???????????? token=test ?????????????????????????????????????????????
            if (isDev(token)) {
                authInfo = new AuthInfo().setAccount("changqing").setUserId(3L)
                        .setTokenType(BEARER_HEADER_KEY).setName("???????????????");
            }

            // 4, ?????? ??? ?????? token
            if (authInfo == null) {
                authInfo = tokenUtil.getAuthInfo(token);
            }

            if (!isDev(token)) {
                // 5????????? ??????????????????????????????????????????
                String newToken = JwtUtil.getToken(token);
                String tokenKey = CacheKey.buildKey(newToken);
                CacheObject tokenCache = channel.get(CacheKey.TOKEN_USER_ID, tokenKey);
                if (tokenCache.getValue() == null) {
                    // ????????????????????????????????????T??????bug?????? bug ???????????????????????????????????????UserTokenService.save ???????????????
                } else if (StrUtil.equals(BizConstant.LOGIN_STATUS, (String) tokenCache.getValue())) {
                    return errorResponse(response, JWT_OFFLINE.getMsg(), JWT_OFFLINE.getCode(), 200);
                }
            }
        } catch (BizException e) {
            return errorResponse(response, e.getMessage(), e.getCode(), 200);
        } catch (Exception e) {
            return errorResponse(response, "??????token??????", R.FAIL_CODE, 200);
        }

        //6, ???????????? token ??????????????????????????? ??? ????????????tenant???Authorization ????????????????????????
        if (authInfo != null) {
            addHeader(mutate, BaseContextConstants.JWT_KEY_ACCOUNT, authInfo.getAccount());
            addHeader(mutate, BaseContextConstants.JWT_KEY_USER_ID, authInfo.getUserId());
            addHeader(mutate, BaseContextConstants.JWT_KEY_NAME, authInfo.getName());

            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(authInfo.getUserId()));
        }

        ServerHttpRequest build = mutate.build();
        return chain.filter(exchange.mutate().request(build).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mutate.header(name, valueEncode);
    }

    protected Mono<Void> errorResponse(ServerHttpResponse response, String errMsg, int errCode, int httpStatusCode) {
        R tokenError = R.fail(errCode, errMsg);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBuffer dataBuffer = response.bufferFactory().wrap(tokenError.toString().getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

}
