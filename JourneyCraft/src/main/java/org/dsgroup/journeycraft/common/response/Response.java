package org.dsgroup.journeycraft.common.response;

import lombok.Data;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;

import java.io.Serializable;

/**
 * 统一返回结果类
 * <p>
 * 根据接口文档规范，所有接口响应统一采用以下格式：
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {}
 * }
 */
@Data
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public Response() {
    }

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Response<T> ok() {
        return new Response<>(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> Response<T> ok(T data) {
        return new Response<>(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(ResponseCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回（使用错误码）
     */
    public static <T> Response<T> error(ResponseCodeEnum responseCodeEnum) {
        return new Response<>(responseCodeEnum.getCode(), responseCodeEnum.getMessage(), null);
    }

    /**
     * 失败返回（自定义错误码和消息）
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 失败返回（使用错误码，带自定义消息）
     */
    public static <T> Response<T> error(ResponseCodeEnum responseCodeEnum, String message) {
        return new Response<>(responseCodeEnum.getCode(), message, null);
    }

    /**
     * 失败返回（直接传消息，使用 500 错误码）
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(ResponseCodeEnum.INTERNAL_ERROR.getCode(), message, null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == ResponseCodeEnum.SUCCESS.getCode();
    }

}
