package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色实体类
 * <p>
 * 对应数据库表：sys_role
 * 功能说明：封装角色信息，用于RBAC权限控制
 * </p>
 */
@Data
@TableName("sys_role")
public class SysRole {

    /**
     * 角色ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称（如：超级管理员）
     */
    private String roleName;

    /**
     * 角色编码（如：ROLE_ADMIN）
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（1：正常 0：禁用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（1：已删除 0：未删除）
     */
    @TableLogic
    private Integer deleted;
}