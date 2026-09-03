package com.example.demo.common;

/**
 * 统一响应码枚举
 * <p>
 * 所有业务响应码统一管理，前端根据 code 判断请求是否成功。
 * 类比前端：axios 拦截器中根据 code 跳转登录页或弹错误提示。
 * </p>
 */
public enum ResponseCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 参数校验失败 */
    BAD_REQUEST(400, "参数校验失败"),

    /** 未登录 / Token无效 */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /** 无权限 */
    FORBIDDEN(403, "权限不足，无法访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "请求资源不存在"),

    /** 业务异常 */
    BUSINESS_ERROR(500, "业务处理异常"),

    /** 系统异常 */
    SYSTEM_ERROR(9999, "系统繁忙，请稍后重试"),

    /** 用户名或密码错误 */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /** 用户已被禁用 */
    USER_DISABLED(1002, "用户已被禁用，请联系管理员"),

    /** Token过期 */
    TOKEN_EXPIRED(1003, "登录已过期，请重新登录");

    /** 响应码 */
    private final int code;

    /** 响应提示信息 */
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
