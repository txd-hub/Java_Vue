package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色请求参数DTO
 * <p>
 * 用于创建和更新角色时的参数校验。
 * </p>
 */
@Data
@Schema(description = "角色请求参数")
public class RoleRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最长50个字符")
    @Schema(description = "角色名称", example = "普通用户")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码最长50个字符")
    @Schema(description = "角色编码", example = "ROLE_USER")
    private String roleCode;

    @Size(max = 200, message = "描述最长200个字符")
    @Schema(description = "角色描述", example = "普通用户角色，拥有基础权限")
    private String description;
}
