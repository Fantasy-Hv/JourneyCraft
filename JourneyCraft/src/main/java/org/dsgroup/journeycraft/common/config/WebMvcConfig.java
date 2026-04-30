package org.dsgroup.journeycraft.common.config;

import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.interceptor.TokenAuthInterceptor;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenSessionStore tokenSessionStore;

    /**
     * 注册 Token 拦截器。
     */
    @Bean
    public TokenAuthInterceptor tokenAuthInterceptor() {
        return new TokenAuthInterceptor(tokenSessionStore);
    }

    /**
     * 添加请求拦截器并配置放行路径。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenAuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
                );
    }
}
