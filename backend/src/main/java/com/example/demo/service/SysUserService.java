package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.common.UserCreateRequest;
import com.example.demo.common.UserUpdateRequest;
import com.example.demo.entity.SysUser;

import java.util.Map;

/**
 * 系统用户Service接口
 * <p>
 * 继承IService：获得MyBatis-Plus提供的业务层基础方法
 * 功能说明：定义用户业务层接口，包括登录、获取用户信息、用户CRUD等核心方法
 * </p>
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * <p>
     * 功能说明：
     * 1. 验证用户名和密码
     * 2. 密码BCrypt加密验证
     * 3. 生成登录凭证（Sa-Token）
     * </p>
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token和用户信息）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 获取当前登录用户信息
     * <p>
     * 功能说明：
     * 1. 根据用户ID查询用户信息
     * 2. 包含用户角色信息
     * </p>
     *
     * @param userId 用户ID
     * @return 用户信息（包含角色）
     */
    SysUser getUserInfo(Long userId);

    /**
     * 用户退出登录
     * <p>
     * 功能说明：
     * 清除登录状态，使token失效
     * </p>
     */
    void logout();

    /**
     * 分页查询用户列表
     *
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @param username 用户名（模糊查询，可为空）
     * @return 分页结果
     */
    Page<SysUser> getUserPage(Integer pageNum, Integer pageSize, String username);

    /**
     * 根据ID查询用户详情（含角色信息）
     */
    SysUser getUserDetailById(Long id);

    /**
     * 新增用户
     */
    void createUser(UserCreateRequest request);

    /**
     * 更新用户信息
     */
    void updateUser(UserUpdateRequest request);

    /**
     * 删除用户（逻辑删除）
     */
    void deleteUser(Long id);

    /**
     * 绑定用户角色
     */
    void bindUserRole(Long userId, Long roleId);
}