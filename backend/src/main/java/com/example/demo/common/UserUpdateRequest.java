package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求参数DTO
 * <p>
 * 与创建请求的区别：id 必填，password 非必填（不传则不修改密码）。
 * </p>
 */
@Data
@Schema(description = "更新用户请求参数")
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Size(min = 2, max = 30, message = "用户名长度为2-30个字符")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Size(min = 6, max = 50, message = "密码长度为6-50个字符")
    @Schema(description = "密码（不传则不修改）", example = "123456")
    private String password;

    @Size(max = 50, message = "昵称最长50个字符")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Size(max = 20, message = "手机号最长20个字符")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "状态（1正常 0禁用）", example = "1")
    private Integer status;

    @Schema(description = "角色ID", example = "2")
    private Long roleId;
}
