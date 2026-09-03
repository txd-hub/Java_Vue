package com.example.demo.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.BusinessException;
import com.example.demo.common.RedisUtil;
import com.example.demo.common.UserCreateRequest;
import com.example.demo.common.UserUpdateRequest;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.entity.SysUserRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import com.example.demo.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
 * 5. 用户CRUD管理
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor  // Lombok注解：自动生成带参数的构造函数，用于依赖注入
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    // 密码编码器（BCrypt加密）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注入Mapper依赖
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    // Redis 工具类（缓存操作）
    private final RedisUtil redisUtil;
    // RedisTemplate（用于 keys 命令等高级操作）
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    // Redis 缓存 key 前缀
    private static final String CACHE_USER_LIST = "demo:user:list:";
    private static final String CACHE_USER_DETAIL = "demo:user:detail:";
    private static final String CACHE_NULL = "NULL"; // 空值缓存标记

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

        // 6. 将 Token 存入 Redis（30天过期）
        try {
            String tokenKey = "demo:token:" + user.getId();
            redisUtil.set(tokenKey, token, 30 * 24 * 3600);
            log.info("Token 已存入 Redis: key={}", tokenKey);
        } catch (Exception e) {
            log.warn("Token 存入 Redis 失败，不影响登录: {}", e.getMessage());
        }

        // 7. 构建返回结果
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
     * 1. 删除 Redis 中的 Token
     * 2. 调用Sa-Token的注销方法，清除登录状态
     * </p>
     */
    @Override
    public void logout() {
        try {
            // 获取当前登录用户的 ID，删除 Redis 中的 Token
            long userId = StpUtil.getLoginIdAsLong();
            String tokenKey = "demo:token:" + userId;
            redisUtil.delete(tokenKey);
            log.info("退出登录：已删除 Redis 中的 Token, userId={}", userId);
        } catch (Exception e) {
            log.warn("退出登录时删除 Redis Token 失败: {}", e.getMessage());
        }
        StpUtil.logout();
    }

    // ==================== 用户CRUD方法 ====================

    /**
     * 分页查询用户列表（带 Redis 缓存）
     * <p>
     * 缓存策略：优先从 Redis 读取，缓存未命中则查询 MySQL 并写入 Redis。
     * 缓存穿透防护：查询结果为空时，也会缓存一个空值标记，防止恶意攻击。
     * Redis 不可用时自动降级为直接查询数据库。
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Page<SysUser> getUserPage(Integer pageNum, Integer pageSize, String username) {
        String cacheKey = CACHE_USER_LIST + pageNum + ":" + pageSize + ":" +
                (username != null ? username.trim() : "");

        // 尝试从 Redis 读取缓存
        try {
            Object cached = redisUtil.get(cacheKey);
            if (cached != null) {
                if (CACHE_NULL.equals(cached)) {
                    log.info("用户列表缓存命中空值: key={}", cacheKey);
                    return new Page<>(pageNum, pageSize);
                }
                log.info("用户列表缓存命中: key={}", cacheKey);
                return (Page<SysUser>) cached;
            }
        } catch (Exception e) {
            log.warn("Redis 读取失败，降级查询数据库: {}", e.getMessage());
        }

        // 查询 MySQL
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(SysUser::getUsername, username.trim());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = baseMapper.selectPage(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));

        // 尝试写入 Redis 缓存
        try {
            if (result.getRecords().isEmpty()) {
                redisUtil.set(cacheKey, CACHE_NULL, 10);
            } else {
                redisUtil.set(cacheKey, result, 60);
            }
        } catch (Exception e) {
            log.warn("Redis 写入失败，不影响查询结果: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 根据ID查询用户详情（含角色信息）
     */
    @Override
    public SysUser getUserDetailById(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null); // 不返回密码

        // 查询角色
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, id);
        List<SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);

        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SysRole::getId, roleIds);
            List<SysRole> roles = roleMapper.selectList(roleWrapper);
            user.setRoles(roles);
            user.setRoleIds(roleIds);
        }

        return user;
    }

    /**
     * 新增用户
     * <p>
     * 1. 检查用户名唯一性
     * 2. 密码BCrypt加密存储
     * 3. 插入用户记录
     * 4. 如果指定了角色，绑定用户-角色关系
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateRequest request) {
        // 检查用户名唯一性
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在：" + request.getUsername());
        }

        // 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt加密
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        baseMapper.insert(user);

        // 绑定角色（如果有指定）
        if (request.getRoleId() != null) {
            bindUserRole(user.getId(), request.getRoleId());
        } else {
            // 默认绑定普通用户角色（ROLE_USER）
            bindUserRole(user.getId(), 2L);
        }

        log.info("新增用户成功: {}", user.getUsername());
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        SysUser user = this.getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了用户名，检查唯一性
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, request.getUsername());
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("用户名已存在：" + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // 更新其他字段
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        // 如果传了密码，则更新密码（BCrypt加密）
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        baseMapper.updateById(user);

        // 更新角色绑定
        if (request.getRoleId() != null) {
            bindUserRole(user.getId(), request.getRoleId());
        }

        // 刷新用户列表缓存（清除所有相关缓存）
        refreshUserListCache();

        log.info("更新用户成功: id={}", user.getId());
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 禁止删除超级管理员
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("禁止删除超级管理员");
        }

        baseMapper.deleteById(id);

        // 同时删除用户-角色关联关系
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        userRoleMapper.delete(wrapper);

        // 刷新用户列表缓存
        refreshUserListCache();

        log.info("删除用户成功: id={}, username={}", id, user.getUsername());
    }

    /**
     * 绑定用户角色（一个用户绑定一个角色）
     * <p>
     * 先删除旧的绑定关系，再插入新的。保证一个用户只有一个角色。
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUserRole(Long userId, Long roleId) {
        // 检查用户是否存在
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 删除旧的绑定关系
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 插入新的绑定关系
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

        log.info("绑定用户角色成功: userId={}, roleCode={}", userId, role.getRoleCode());
    }

    /**
     * 刷新用户列表缓存
     * <p>
     * 在用户新增、更新、删除后调用，清除所有用户列表相关的 Redis 缓存。
     * 简单实现：使用 keys 模式匹配删除，生产环境建议使用 Redisson 分布式锁。
     * </p>
     */
    private void refreshUserListCache() {
        try {
            // 使用 Spring RedisTemplate 直接操作 keys 命令
            Set<String> keys = redisTemplate.keys(CACHE_USER_LIST + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已清除用户列表缓存，共 {} 个 key", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除用户列表缓存失败: {}", e.getMessage());
        }
    }
}