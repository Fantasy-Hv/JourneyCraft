package org.dsgroup.journeycraft.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * <p>
 * 开发环境配置：禁用认证以便测试API
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（开发环境）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置授权规则 - 更详细的路径配置
            .authorizeHttpRequests(authorize -> authorize
                // 允许API文档相关路径
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-ui-index.html",
                    "/v3/api-docs/**",
                    "/v2/api-docs",
                    "/doc.html",
                    "/webjars/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                // 其他所有请求都放行（开发环境）
                .anyRequest().permitAll()
            )
            
            // 禁用HTTP Basic认证
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // 禁用表单登录（开发环境）
            .formLogin(AbstractHttpConfigurer::disable);
        
        return http.build();
    }
}
