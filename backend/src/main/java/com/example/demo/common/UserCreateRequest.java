package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建用户请求参数DTO
 * <p>
 * 使用 @Valid 注解实现参数校验，Controller 层接收后自动校验。
 * 类比前端：表单验证规则 required / minLength / pattern。
 * </p>
 */
@Data
@Schema(description = "创建用户请求参数")
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 30, message = "用户名长度为2-30个字符")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度为6-50个字符")
    @Schema(description = "密码", example = "123456")
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

    @Schema(description = "角色ID，不传则默认普通用户", example = "2")
    private Long roleId;
}
