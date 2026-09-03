package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.BusinessException;
import com.example.demo.common.RoleRequest;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUserRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import com.example.demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色Service实现类
 * <p>
 * 实现角色管理的所有业务逻辑。
 * </p>
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;

    public SysRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    /**
     * 查询所有有效角色（排除已逻辑删除和禁用的）
     */
    @Override
    public List<SysRole> listRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getId);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 分页查询角色列表
     */
    @Override
    public Page<SysRole> getRolePage(Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysRole::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询角色详情
     */
    @Override
    public SysRole getRoleById(Long id) {
        SysRole role = baseMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 新增角色
     * <p>
     * 校验角色编码唯一性后插入数据库。
     * </p>
     */
    @Override
    public void createRole(RoleRequest request) {
        // 检查角色编码唯一性
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, request.getRoleCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在：" + request.getRoleCode());
        }

        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(1);
        baseMapper.insert(role);
        log.info("新增角色成功: {}", role.getRoleCode());
    }

    /**
     * 更新角色信息
     */
    @Override
    public void updateRole(Long id, RoleRequest request) {
        SysRole role = getRoleById(id);

        // 如果修改了角色编码，检查唯一性
        if (!role.getRoleCode().equals(request.getRoleCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, request.getRoleCode());
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("角色编码已存在：" + request.getRoleCode());
            }
        }

        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        baseMapper.updateById(role);
        log.info("更新角色成功: id={}", id);
    }

    /**
     * 删除角色（逻辑删除）
     * <p>
     * 检查是否有用户绑定该角色，有则禁止删除。
     * </p>
     */
    @Override
    public void deleteRole(Long id) {
        getRoleById(id); // 确保存在

        // 检查是否有用户绑定此角色
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, id);
        if (sysUserRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该角色下存在用户，无法删除");
        }

        baseMapper.deleteById(id);
        log.info("删除角色成功: id={}", id);
    }
}
