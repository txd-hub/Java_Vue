package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域CORS配置类
 * <p>
 * 功能说明：
 * 1. 允许前端Vue开发服务器（localhost:5173）访问后端接口
 * 2. 配置允许的请求方式（GET、POST、PUT、DELETE等）
 * 3. 配置允许的请求头（包括Authorization用于token认证）
 * </p>
 */
@Configuration
public class CorsConfig {

    /**
     * 创建跨域过滤器
     * <p>
     * 功能说明：
     * 全局配置CORS，解决前端Vue访问后端接口的跨域问题
     * </p>
     *
     * @return CorsFilter 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        // CORS配置
        CorsConfiguration config = new CorsConfiguration();

        // 允许的来源（前端Vue开发服务器地址）
        config.addAllowedOriginPattern("*");

        // 允许的请求方式
        config.addAllowedMethod("*");

        // 允许的请求头（包括Authorization用于token认证）
        config.addAllowedHeader("*");

        // 允许携带cookie（如果需要）
        config.setAllowCredentials(true);

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        // 映射路径，拦截所有请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}