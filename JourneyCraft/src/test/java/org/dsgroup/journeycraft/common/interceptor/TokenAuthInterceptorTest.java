package org.dsgroup.journeycraft.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Token 校验拦截器单元测试。
 */
class TokenAuthInterceptorTest {

    private TokenSessionStore tokenSessionStore;
    private TokenAuthInterceptor interceptor;

    @BeforeEach
    void setUp() {
        tokenSessionStore = new TokenSessionStore();
        interceptor = new TokenAuthInterceptor(tokenSessionStore);
    }

    @Test
    void preHandleShouldAllowValidToken() {
        TokenSessionStore.TokenPair tokenPair = tokenSessionStore.issue(1001L, 60, 600);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/info");
        request.addHeader("Authorization", "Bearer " + tokenPair.accessToken());
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
        assertEquals(1001L, request.getAttribute("currentUserId"));
    }

    @Test
    void preHandleShouldRejectMissingToken() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/info");
        HttpServletResponse response = new MockHttpServletResponse();

        assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, new Object()));
    }
}
