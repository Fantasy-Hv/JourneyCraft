package org.dsgroup.journeycraft.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类（开发环境）
 * 仅供本分支测试用，后续应由负责auth模块的代码代替。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 允许 diary 接口匿名访问（开发环境）
                .requestMatchers("/api/diary/**").permitAll()
                // 允许 swagger/knife4j 访问
                .requestMatchers("/doc.html", "/doc/**", "/api-docs/**", "/webjars/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
