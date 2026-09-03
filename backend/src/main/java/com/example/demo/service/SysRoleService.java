package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.common.RoleRequest;
import com.example.demo.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 * <p>
 * 定义角色管理的业务方法，实现类负责具体逻辑。
 * </p>
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 查询所有角色列表（用于下拉选择）
     */
    List<SysRole> listRoles();

    /**
     * 分页查询角色列表
     */
    Page<SysRole> getRolePage(Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询角色详情
     */
    SysRole getRoleById(Long id);

    /**
     * 新增角色
     */
    void createRole(RoleRequest request);

    /**
     * 更新角色信息
     */
    void updateRole(Long id, RoleRequest request);

    /**
     * 删除角色（逻辑删除）
     */
    void deleteRole(Long id);
}
