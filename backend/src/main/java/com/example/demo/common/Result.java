package com.example.demo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果体
 * <p>
 * 功能说明：
 * 1. 封装所有接口的返回格式
 * 2. 统一状态码、消息、数据结构
 * 3. 提供静态方法快速构建成功/失败响应
 * </p>
 *
 * @param <T> 泛型：返回数据的类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     * 200：成功
     * 500：失败
     */
    private Integer code;

    /**
     * 消息提示
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 无参构造方法
     */
    public Result() {
    }

    /**
     * 全参构造方法
     *
     * @param code    状态码
     * @param message 消息提示
     * @param data    返回数据
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     *
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 返回数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回（带消息和数据）
     *
     * @param message 消息提示
     * @param data    返回数据
     * @return Result对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败返回（默认消息）
     *
     * @return Result对象
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    /**
     * 失败返回（带自定义消息）
     *
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败返回（带状态码和消息）
     *
     * @param code    状态码
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}