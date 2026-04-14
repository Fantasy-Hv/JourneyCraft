package org.dsgroup.journeycraft.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 校验拦截器。
 */
@RequiredArgsConstructor
public class TokenAuthInterceptor implements HandlerInterceptor {

    private final TokenSessionStore tokenSessionStore;

    /**
     * 在请求进入控制器前校验 Token。
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        Long userId = tokenSessionStore.requireUserId(authorization);
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentAuthorization", authorization);
        return true;
    }
}
