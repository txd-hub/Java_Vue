package com.example.demo.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.entity.SysUserRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import com.example.demo.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户Service实现类
 * <p>
 * 实现SysUserService接口，提供具体的业务逻辑
 * 功能说明：
 * 1. 用户登录认证
 * 2. 密码BCrypt加密验证
 * 3. 用户信息查询
 * 4. 登录状态管理
 * </p>
 */
@Service
@RequiredArgsConstructor  // Lombok注解：自动生成带参数的构造函数，用于依赖注入
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    // 密码编码器（BCrypt加密）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注入Mapper依赖
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * <p>
     * 流程说明：
     * 1. 根据用户名查询用户
     * 2. 验证密码是否正确（BCrypt加密比对）
     * 3. 检查用户状态是否正常
     * 4. 使用Sa-Token生成登录凭证
     * 5. 返回token和用户基本信息
     * </p>
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = this.getOne(wrapper);

        // 2. 验证用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证密码是否正确（BCrypt加密比对）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 5. 使用Sa-Token登录，生成token
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        // 用户基本信息（不包含密码）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());

        // 获取用户角色
        List<String> roleCodes = userMapper.selectRoleCodesByUserId(user.getId());
        userInfo.put("roles", roleCodes);

        result.put("userInfo", userInfo);

        return result;
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 流程说明：
     * 1. 根据用户ID查询用户基本信息
     * 2. 查询用户拥有的角色列表
     * 3. 组装完整用户信息返回
     * </p>
     */
    @Override
    public SysUser getUserInfo(Long userId) {
        // 1. 查询用户基本信息
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 查询用户角色ID列表
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);

        if (!userRoles.isEmpty()) {
            // 3. 查询角色详细信息
            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SysRole::getId, roleIds);
            List<SysRole> roles = roleMapper.selectList(roleWrapper);

            // 4. 设置用户角色信息
            user.setRoles(roles);
            user.setRoleIds(roleIds);
        }

        return user;
    }

    /**
     * 用户退出登录
     * <p>
     * 功能说明：
     * 调用Sa-Token的注销方法，清除登录状态
     * </p>
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }
}