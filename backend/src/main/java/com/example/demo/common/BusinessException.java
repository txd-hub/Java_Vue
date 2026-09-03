package com.example.demo.common;

/**
 * 自定义业务异常类
 * <p>
 * 用于在业务逻辑中主动抛出带有明确提示信息的异常。
 * 类比前端：throw new Error('用户名已存在')，全局异常处理器捕获后返回标准 JSON 给前端。
 * </p>
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final int code;

    /** 错误信息 */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
