package org.dsgroup.journeycraft.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger 配置
 * <p>
 * 配置 Knife4j OpenAPI 3 规范
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JourneyCraft 智能旅游推荐系统 API")
                        .version("1.0.0")
                        .description("导航模块、景区模块、社交模块等核心API文档")
                        .contact(new Contact()
                                .name("JourneyCraft 开发团队")
                                .email("dev@journeycraft.com")));
    }
}
