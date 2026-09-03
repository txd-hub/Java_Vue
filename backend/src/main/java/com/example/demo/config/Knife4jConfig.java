package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j/Swagger 接口文档配置
 * <p>
 * 配置接口文档的基本信息（标题、描述、版本）和 Token 认证方式。
 * 启动后端后访问 http://localhost:8080/doc.html 即可在线调试接口。
 * </p>
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("企业级管理系统 API")
                        .description("Spring Boot 3 + Vue3 + MyBatis-Plus 前后端分离项目接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("Admin").email("admin@example.com")))
                // 添加全局 Token 认证
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Sa-Token")));
    }
}
