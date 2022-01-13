package com.changqing.gov.gateway.filter.zuul;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.changqing.gov.base.R;
import com.changqing.gov.exception.code.ExceptionCode;
import com.changqing.gov.gateway.entity.BlockList;
import com.changqing.gov.gateway.entity.RateLimiter;
import com.changqing.gov.gateway.service.BlockListService;
import com.changqing.gov.gateway.service.RateLimiterService;
import com.changqing.gov.utils.DateUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * @author changqing
 * @date 2020/8/9 下午10:21
 */
@Slf4j
@RequiredArgsConstructor
public class ZuulPreCheckFilter extends ZuulFilter {
    private final BlockListService blockListService;
    private final RateLimiterService rateLimiterService;

    private final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Value("${server.servlet.context-path}")
    protected String zuulPrefix;

    @Override
    public String filterType() {
        // 前置过滤器
        return PRE_TYPE;
    }

    /**
     * filterOrder：通过int值来定义过滤器的执行顺序
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 5;
    }


    /**
     * 返回一个boolean类型来判断该过滤器是否要执行，所以通过此函数可实现过滤器的开关。在上例中，我们直接返回true，所以该过滤器总是生效
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        boolean blockListResult = matchBlockList(ctx);
        if (blockListResult) {
            errorResponse("阻止列表限制，禁止访问", ExceptionCode.UNAUTHORIZED.getCode(), 200);
            return null;
        }
        boolean rateLimiterResult = matchRateLimiter(ctx);
        if (rateLimiterResult) {
            errorResponse("访问频率超限，请稍后再试", ExceptionCode.UNAUTHORIZED.getCode(), 200);
            return null;
        }

        return null;
    }

    private String getUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        uri = StrUtil.subSuf(uri, zuulPrefix.length());
        return uri;
    }

    /**
     * 不允许访问的列表
     */
    public boolean matchBlockList(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();

        String originUri = getUri(request);
        if (originUri != null) {
            String ip = ServletUtil.getClientIP(request);
            String requestMethod = request.getMethod();
            AtomicBoolean forbid = new AtomicBoolean(false);
            // 阻止访问列表
            Set<Object> blockList = blockListService.findBlockList(ip);
            blockList.addAll(blockListService.findBlockList());

            // 路径和请求方式 能匹配上，且限制区间内则禁用
            doBlockListCheck(forbid, blockList, originUri, requestMethod);

            log.info("阻止列表验证完成");
            if (forbid.get()) {
                return true;
            }
        } else {
            log.info("请求地址未正确获取，无法进行阻止列表检查");
        }
        return false;
    }

    private void doBlockListCheck(AtomicBoolean forbid, Set<Object> blockList, String uri, String requestMethod) {
        for (Object o : blockList) {
            BlockList b = (BlockList) o;
            if (!b.getStatus()) {
                continue;
            }
            if (!PATH_MATCHER.match(b.getRequestUri(), uri)) {
                continue;
            }
            if (!BlockList.METHOD_ALL.equalsIgnoreCase(b.getRequestMethod())
                    && !StrUtil.equalsIgnoreCase(requestMethod, b.getRequestMethod())) {
                continue;
            }
            if (StrUtil.isNotBlank(b.getLimitStart()) && StrUtil.isNotBlank(b.getLimitEnd())) {
                if (DateUtils.between(LocalTime.parse(b.getLimitStart()), LocalTime.parse(b.getLimitEnd()))) {
                    forbid.set(true);
                }
            } else {
                forbid.set(true);
            }
            if (forbid.get()) {
                break;
            }
        }
    }


    /**
     * 限流规则匹配
     *
     * @return
     */
    public boolean matchRateLimiter(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();

        String originUri = getUri(request);

        if (originUri == null) {
            return false;
        }
        String requestMethod = request.getMethod();
        String requestIp = ServletUtil.getClientIP(request);
        RateLimiter rule = rateLimiterService.getRateLimiter(originUri, RateLimiter.METHOD_ALL);
        if (rule == null) {
            rule = rateLimiterService.getRateLimiter(originUri, requestMethod);
        }
        if (rule != null) {
            AtomicBoolean limit = new AtomicBoolean(false);
            boolean result = rateLimiterCheck(limit, rule, originUri, requestIp, requestMethod);
            log.info("限流验证已完成");
            if (result) {
                return true;
            }
        }
        return false;
    }

    private boolean rateLimiterCheck(AtomicBoolean limit, RateLimiter rule, String uri, String requestIp, String requestMethod) {
        boolean isRateLimiterHit = rule.getStatus()
                && (RateLimiter.METHOD_ALL.equalsIgnoreCase(rule.getRequestMethod()) || StrUtil.equalsIgnoreCase(requestMethod, rule.getRequestMethod()));
        if (isRateLimiterHit) {
            if (StrUtil.isNotBlank(rule.getLimitStart()) && StrUtil.isNotBlank(rule.getLimitEnd())) {
                if (DateUtils.between(LocalTime.parse(rule.getLimitStart()), LocalTime.parse(rule.getLimitEnd()))) {
                    limit.set(true);
                }
            } else {
                limit.set(true);
            }
        }
        if (limit.get()) {
            String requestUri = uri;
            int count = rateLimiterService.getCurrentRequestCount(requestUri, requestIp);
            if (count == 0) {
                rateLimiterService.setCurrentRequestCount(requestUri, requestIp, rule.getIntervalSec());
            } else if (count >= rule.getCount()) {
                return true;
            } else {
                rateLimiterService.incrCurrentRequestCount(requestUri, requestIp);
            }
        }
        return false;
    }

    /**
     * 网关抛异常
     *
     * @param body
     * @param code
     */
    protected void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        // 返回错误码
        ctx.setResponseStatusCode(code);
        ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
        if (ctx.getResponseBody() == null) {
            // 返回错误内容
            ctx.setResponseBody(body);
            // 过滤该请求，不对其进行路由
            ctx.setSendZuulResponse(false);
        }
    }

    protected void errorResponse(String errMsg, int errCode, int httpStatusCode) {
        R tokenError = R.fail(errCode, errMsg);
        setFailedRequest(tokenError.toString(), httpStatusCode);
    }
}
