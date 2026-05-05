package org.dsgroup.journeycraft.common.config;

import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.interceptor.RequestLoggingInterceptor;
import org.dsgroup.journeycraft.common.interceptor.TokenAuthInterceptor;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
     * 注册请求日志拦截器。
     */
    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
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
                        "/api/user/**",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
                );
        registry.addInterceptor(requestLoggingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
                );

    }
    /**
     * 配置跨域映射
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 允许的源地址模式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许的请求头
                .allowCredentials(true)  // 是否允许发送Cookie
                .maxAge(3600);  // 预检请求的有效期
    }
}
