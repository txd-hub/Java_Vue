package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户实体类
 * <p>
 * 对应数据库表：sys_user
 * 功能说明：封装用户信息，包含基本字段和角色信息
 * </p>
 */
@Data
@TableName("sys_user")
public class SysUser {

    /**
     * 用户ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     * @JsonIgnore: 序列化时忽略此字段，防止密码泄露
     */
    @JsonIgnore
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态（1：正常 0：禁用）
     */
    private Integer status;

    /**
     * 创建时间
     * @JsonFormat: 格式化日期显示
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
     * @TableLogic: MyBatis-Plus逻辑删除注解
     */
    @TableLogic
    private Integer deleted;

    /**
     * 用户拥有的角色列表（非数据库字段）
     * @TableField(exist = false): 告诉MyBatis-Plus这不是数据库字段
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 用户角色ID列表（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private List<Long> roleIds;
}