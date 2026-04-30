package org.dsgroup.journeycraft.navigation.controller;

import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.result.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制器基类
 * <p>
 * 提供统一的响应格式和日志记录
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
public abstract class BaseController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 成功响应
     */
    protected <T> Response<T> success() {
        return Response.ok();
    }

    /**
     * 成功响应（带数据）
     */
    protected <T> Response<T> success(T data) {
        return Response.ok(data);
    }

    /**
     * 成功响应（带消息）
     */
    protected Response<Void> success(String message) {
        return Response.ok(message, null);
    }

    /**
     * 成功响应（带消息和数据）
     */
    protected <T> Response<T> success(String message, T data) {
        return Response.ok(message, data);
    }

    /**
     * 失败响应
     */
    protected <T> Response<T> fail() {
        return Response.error("操作失败");
    }

    /**
     * 失败响应（带消息）
     */
    protected <T> Response<T> fail(String message) {
        return Response.error(message);
    }

    /**
     * 失败响应（带错误码和消息）
     */
    protected <T> Response<T> fail(ResponseCodeEnum codeEnum) {
        return Response.error(codeEnum);
    }

    /**
     * 失败响应（带错误码和消息）
     */
    protected <T> Response<T> fail(Integer code, String message) {
        return Response.error(code, message);
    }

    /**
     * 参数错误响应
     */
    protected <T> Response<T> paramError() {
        return Response.error(ResponseCodeEnum.BAD_REQUEST);
    }

    /**
     * 参数错误响应（带消息）
     */
    protected <T> Response<T> paramError(String message) {
        return Response.error(ResponseCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 未授权响应
     */
    protected <T> Response<T> unauthorized() {
        return Response.error(ResponseCodeEnum.UNAUTHORIZED);
    }

    /**
     * 未找到响应
     */
    protected <T> Response<T> notFound() {
        return Response.error(ResponseCodeEnum.NOT_FOUND);
    }

    /**
     * 未找到响应（带消息）
     */
    protected <T> Response<T> notFound(String message) {
        return Response.error(ResponseCodeEnum.NOT_FOUND.getCode(), message);
    }

    /**
     * 服务器错误响应
     */
    protected <T> Response<T> serverError() {
        return Response.error(ResponseCodeEnum.INTERNAL_ERROR);
    }

    /**
     * 服务器错误响应（带消息）
     */
    protected <T> Response<T> serverError(String message) {
        return Response.error(ResponseCodeEnum.INTERNAL_ERROR.getCode(), message);
    }
}