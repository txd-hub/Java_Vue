package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.RoleRequest;
import com.example.demo.entity.SysRole;
import com.example.demo.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * <p>
 * RESTful API 设计规范：
 * GET    /api/roles          -- 查询角色列表
 * GET    /api/roles/{id}     -- 根据ID查询角色详情
 * POST   /api/roles          -- 新增角色
 * PUT    /api/roles/{id}     -- 更新角色信息
 * DELETE /api/roles/{id}     -- 删除角色（逻辑删除）
 * </p>
 */
@Tag(name = "角色管理", description = "角色CRUD接口")
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 查询角色列表（不分页，用于下拉选择）
     */
    @Operation(summary = "查询角色列表")
    @GetMapping
    public Result<List<SysRole>> list() {
        List<SysRole> roles = sysRoleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public Result<Page<SysRole>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysRole> page = sysRoleService.getRolePage(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据ID查询角色详情
     */
    @Operation(summary = "根据ID查询角色详情")
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getRoleById(id);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleRequest request) {
        sysRoleService.createRole(request);
        return Result.success();
    }

    /**
     * 更新角色信息
     */
    @Operation(summary = "更新角色信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody RoleRequest request) {
        sysRoleService.updateRole(id, request);
        return Result.success();
    }

    /**
     * 删除角色（逻辑删除）
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }
}
