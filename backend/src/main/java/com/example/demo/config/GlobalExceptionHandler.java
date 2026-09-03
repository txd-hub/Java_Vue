package com.example.demo.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.example.demo.common.BusinessException;
import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 功能说明：
 * 1. 捕获所有Controller层抛出的异常
 * 2. 统一返回格式，避免前端收到不友好的错误信息
 * 3. 处理Sa-Token认证异常
 * 4. 处理参数校验异常
 * 5. 处理自定义业务异常
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理Sa-Token未登录异常
     * <p>
     * 当用户未登录访问需要登录的接口时抛出此异常
     * </p>
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        String message;
        // 根据不同类型的未登录异常返回不同消息
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未提供登录凭证";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "登录凭证无效";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "登录凭证已过期";
        } else {
            message = "未登录或登录已过期";
        }
        return Result.error(401, message);
    }

    /**
     * 处理Sa-Token无权限异常
     * <p>
     * 当用户没有访问某个接口的权限时抛出此异常
     * </p>
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotPermissionException(NotPermissionException e) {
        return Result.error(403, "没有权限访问：" + e.getPermission());
    }

    /**
     * 处理Sa-Token无角色异常
     * <p>
     * 当用户没有某个角色时抛出此异常
     * </p>
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRoleException(NotRoleException e) {
        return Result.error(403, "没有角色权限：" + e.getRole());
    }

    /**
     * 处理自定义业务异常
     * <p>
     * 业务代码中主动抛出的异常，携带明确的业务错误码和提示信息。
     * 例如：用户名已存在、参数不符合业务规则等。
     * </p>
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid注解校验失败）
     * <p>
     * 当请求参数不符合校验规则时抛出此异常
     * </p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 获取所有字段错误信息，拼接成一条消息
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(400, "参数校验失败：" + message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(400, "参数绑定失败：" + message);
    }

    /**
     * 处理业务逻辑异常
     * <p>
     * 当业务逻辑执行过程中抛出RuntimeException时捕获
     * </p>
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error("业务异常：" + e.getMessage());
    }

    /**
     * 处理所有其他未捕获的异常
     * <p>
     * 作为兜底异常处理器，捕获所有未被上面方法捕获的异常
     * </p>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error("系统异常：" + e.getMessage());
    }
}