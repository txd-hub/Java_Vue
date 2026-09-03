package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.UserCreateRequest;
import com.example.demo.common.UserRoleRequest;
import com.example.demo.common.UserUpdateRequest;
import com.example.demo.entity.SysUser;
import com.example.demo.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * <p>
 * RESTful API 设计规范：
 * GET    /api/users          -- 分页查询用户列表
 * GET    /api/users/{id}     -- 根据ID查询用户详情
 * POST   /api/users          -- 新增用户
 * PUT    /api/users/{id}     -- 更新用户信息
 * DELETE /api/users/{id}     -- 删除用户（逻辑删除）
 * PUT    /api/users/role     -- 绑定用户角色
 * </p>
 */
@Tag(name = "用户管理", description = "用户CRUD接口")
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public Result<Page<SysUser>> list(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名（模糊查询）") @RequestParam(required = false) String username) {
        Page<SysUser> page = sysUserService.getUserPage(pageNum, pageSize, username);
        return Result.success(page);
    }

    /**
     * 根据ID查询用户详情
     */
    @Operation(summary = "根据ID查询用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getUserDetailById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     * <p>
     * @Valid 启动参数校验：username 非空、password 非空、email 格式校验等。
     * 类比前端：element-plus 表单 rules 验证。
     * </p>
     */
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
        sysUserService.createUser(request);
        return Result.success();
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequest request) {
        request.setId(id);
        sysUserService.updateUser(request);
        return Result.success();
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    /**
     * 绑定用户角色
     * <p>
     * 一个用户绑定一个角色，重复绑定会覆盖之前的角色。
     * </p>
     */
    @Operation(summary = "绑定用户角色")
    @PutMapping("/role")
    public Result<Void> bindRole(@Valid @RequestBody UserRoleRequest request) {
        sysUserService.bindUserRole(request.getUserId(), request.getRoleId());
        return Result.success();
    }
}
