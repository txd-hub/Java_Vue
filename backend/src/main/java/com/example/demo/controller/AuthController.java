package com.example.demo.controller;

import com.example.demo.common.LoginRequest;
import com.example.demo.common.Result;
import com.example.demo.entity.SysUser;
import com.example.demo.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 功能说明：
 * 处理用户登录、退出、获取用户信息等认证相关请求
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、退出、获取用户信息")
public class AuthController {

    private final SysUserService userService;

    /**
     * 用户登录
     * <p>
     * 请求方式：POST
     * 请求路径：/api/auth/login
     * 功能说明：验证用户名密码，返回token
     * </p>
     *
     * @param request 登录请求参数（用户名、密码）
     * @return 包含token的登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名密码，返回token")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request.getUsername(), request.getPassword());
            return Result.success("登录成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 请求方式：GET
     * 请求路径：/api/auth/info
     * 功能说明：根据token获取当前登录用户的详细信息
     * 需要登录：是（Sa-Token拦截器自动验证）
     * </p>
     *
     * @return 用户信息（包含角色）
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "根据token获取当前登录用户信息")
    public Result<SysUser> getUserInfo() {
        try {
            // 从Sa-Token获取当前登录用户ID
            Long userId = (Long) cn.dev33.satoken.stp.StpUtil.getLoginId();
            SysUser userInfo = userService.getUserInfo(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 用户退出登录
     * <p>
     * 请求方式：POST
     * 请求路径：/api/auth/logout
     * 功能说明：清除登录状态，使token失效
     * </p>
     *
     * @return 退出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出", description = "清除登录状态")
    public Result<Void> logout() {
        try {
            userService.logout();
            return Result.success("退出成功", null);
        } catch (Exception e) {
            return Result.error("退出失败：" + e.getMessage());
        }
    }

    /**
     * 测试接口（需要登录）
     * <p>
     * 请求方式：GET
     * 请求路径：/api/auth/test
     * 功能说明：测试Sa-Token登录拦截是否生效
     * </p>
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试Sa-Token登录拦截")
    public Result<String> test() {
        return Result.success("访问成功，您已登录！");
    }
}