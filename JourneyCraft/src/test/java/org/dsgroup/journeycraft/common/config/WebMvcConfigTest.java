package org.dsgroup.journeycraft.common.config;

import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebMvcConfigTest {

    @Test
    void addInterceptorsShouldExcludeUserApis() throws Exception {
        WebMvcConfig config = new WebMvcConfig(new TokenSessionStore());
        InterceptorRegistry registry = new InterceptorRegistry();

        config.addInterceptors(registry);

        Method getInterceptors = InterceptorRegistry.class.getDeclaredMethod("getInterceptors");
        getInterceptors.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Object> interceptors = (List<Object>) getInterceptors.invoke(registry);

        MappedInterceptor mappedInterceptor = interceptors.stream()
                .filter(MappedInterceptor.class::isInstance)
                .map(MappedInterceptor.class::cast)
                .findFirst()
                .orElseThrow();

        assertArrayEquals(new String[]{"/api/**"}, mappedInterceptor.getIncludePathPatterns());
        assertTrue(Arrays.asList(mappedInterceptor.getExcludePathPatterns()).contains("/api/user/**"));
    }
}
