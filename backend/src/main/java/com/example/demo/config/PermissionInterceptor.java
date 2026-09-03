package com.example.demo.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUserRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RBAC 权限拦截器
 * <p>
 * 实现基于角色的接口权限校验：
 * - 管理员（ROLE_ADMIN）可访问全部接口
 * - 普通用户（ROLE_USER）禁止访问管理员专属接口
 * </p>
 * <p>
 * 类比前端：vue-router 的 beforeEach 路由守卫，根据用户角色判断是否有权访问某个页面。
 * </p>
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 管理员专属接口路径前缀
     */
    private static final List<String> ADMIN_PATHS = List.of(
            "/api/users",      // 用户管理
            "/api/roles"       // 角色管理
    );

    public PermissionInterceptor(SysUserMapper userMapper, SysRoleMapper roleMapper,
                                  SysUserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 请求拦截处理
     * <p>
     * 检查当前用户是否有权限访问目标接口。
     * 管理员角色可以访问所有接口，普通用户只能访问非管理员专属接口。
     * </p>
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();

        // 判断是否为管理员专属接口
        boolean isAdminPath = ADMIN_PATHS.stream().anyMatch(requestUri::startsWith);
        if (!isAdminPath) {
            // 非管理员专属接口，直接放行（登录校验由 Sa-Token 拦截器处理）
            return true;
        }

        // 管理员专属接口：检查当前用户是否为管理员
        try {
            long userId = StpUtil.getLoginIdAsLong();
            List<String> roleCodes = getUserRoleCodes(userId);

            if (roleCodes.contains("ROLE_ADMIN")) {
                // 管理员，放行
                return true;
            } else {
                // 普通用户，拒绝访问
                log.warn("权限不足：用户 {} 尝试访问管理员专属接口 {}", userId, requestUri);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"权限不足，无法访问该接口\",\"data\":null}");
                return false;
            }
        } catch (Exception e) {
            log.error("权限校验异常: {}", e.getMessage());
            // 异常情况放行，由 Sa-Token 拦截器处理登录校验
            return true;
        }
    }

    /**
     * 获取用户的角色编码列表
     * <p>
     * 通过三表联查获取用户的所有角色编码。
     * </p>
     */
    private List<String> getUserRoleCodes(Long userId) {
        // 查询用户-角色关联
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色信息
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(SysRole::getId, roleIds);
        List<SysRole> roles = roleMapper.selectList(roleWrapper);

        // 返回角色编码列表
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }
}
