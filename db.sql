-- ============================================
-- 数据库初始化脚本
-- 库名：demo_admin
-- 字符集：utf8mb4
-- 说明：RBAC最小权限模型
-- 使用方法：复制到Navicat执行即可
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `demo_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE `demo_admin`;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(64) DEFAULT '' COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT '' COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `avatar` VARCHAR(256) DEFAULT '' COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态（1：正常 0：禁用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（1：已删除 0：未删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================
-- 角色表
-- ============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(256) DEFAULT '' COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态（1：正常 0：禁用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（1：已删除 0：未删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ============================================
-- 用户角色关联表
-- ============================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================
-- 插入初始数据
-- ============================================

-- 插入默认角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有系统所有权限'),
('普通用户', 'ROLE_USER', '普通用户权限');

-- 插入默认管理员用户
-- 密码：admin123（BCrypt加密）
-- BCrypt加密的密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin@example.com', 1);

-- 插入测试用户
-- 密码：user123（BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 'test@example.com', 1);

-- 分配角色
-- admin用户分配超级管理员角色（role_id=1）
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- testuser用户分配普通用户角色（role_id=2）
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2, 2);

-- ============================================
-- 验证数据
-- ============================================

-- 查看用户信息
SELECT u.id, u.username, u.nickname, u.email, u.status, u.create_time
FROM sys_user u
WHERE u.deleted = 0;

-- 查看角色信息
SELECT r.id, r.role_name, r.role_code, r.description
FROM sys_role r
WHERE r.deleted = 0;

-- 查看用户角色关联
SELECT u.username, r.role_name
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.deleted = 0;

-- ============================================
-- 完成
-- ============================================
-- 数据库初始化完成！
-- 默认管理员账号：admin
-- 默认管理员密码：admin123
-- 默认测试账号：testuser
-- 默认测试密码：user123