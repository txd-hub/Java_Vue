package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户角色绑定请求参数DTO
 * <p>
 * 用于为用户分配角色。
 * </p>
 */
@Data
@Schema(description = "用户角色绑定请求参数")
public class UserRoleRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "2")
    private Long roleId;
}
