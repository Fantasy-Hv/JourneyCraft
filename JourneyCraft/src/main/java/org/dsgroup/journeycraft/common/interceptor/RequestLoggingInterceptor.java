package org.dsgroup.journeycraft.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * 请求日志拦截器，打印请求 URL、参数和处理用时。
 */
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "requestStartTime";

    /**
     * 在请求进入控制器前记录请求信息。
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        String method = request.getMethod();
        String url = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        String params = getRequestParams(request);
        Long userId = (Long) request.getAttribute("currentUserId");

        log.info("[Request] {} {} | params: {} | userId: {}", method, url, params, userId != null ? userId : "anonymous");
        return true;
    }

    /**
     * 在请求完成后记录处理用时。
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long cost = startTime != null ? System.currentTimeMillis() - startTime : 0;
        int status = response.getStatus();

        log.info("[Response] {} {} | cost: {}ms | status: {}", request.getMethod(), request.getRequestURI(), cost, status);
    }

    /**
     * 获取请求参数。
     */
    private String getRequestParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder("{");
        Enumeration<String> paramNames = request.getParameterNames();
        boolean first = true;
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!first) {
                params.append(", ");
            }
            params.append(paramName).append("=").append(request.getParameter(paramName));
            first = false;
        }
        params.append("}");
        return params.toString();
    }
}