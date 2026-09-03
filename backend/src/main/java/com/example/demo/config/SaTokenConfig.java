package com.example.demo.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 * <p>
 * 功能说明：
 * 1. 配置登录拦截规则
 * 2. 定义需要登录的接口
 * 3. 配置排除登录的接口（如登录接口本身）
 * 4. 注册 RBAC 权限拦截器
 * </p>
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;

    public SaTokenConfig(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    /**
     * 注册拦截器
     * <p>
     * 拦截顺序：
     * 1. Sa-Token 拦截器：校验用户是否登录
     * 2. PermissionInterceptor：校验用户是否有权限访问该接口
     * </p>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 注册Sa-Token拦截器（登录校验）
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录校验：所有/api/**接口都需要登录
            SaRouter.match("/api/**")
                    // 排除登录接口和接口文档相关接口
                    .notMatch("/api/auth/login", "/api/auth/logout",
                            "/doc.html", "/webjars/**", "/v3/api-docs/**")
                    // 调用checkLogin()方法进行登录校验
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");

        // 2. 注册 RBAC 权限拦截器（角色校验）
        // 必须在 Sa-Token 拦截器之后，确保用户已登录
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**"
                );
    }
}