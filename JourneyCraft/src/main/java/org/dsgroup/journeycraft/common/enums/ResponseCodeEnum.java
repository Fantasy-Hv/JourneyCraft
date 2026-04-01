package org.dsgroup.journeycraft.common.enums;

import lombok.Getter;

/**
 * 统一错误码枚举
 * <p>
 * 根据接口文档规范定义：
 * 200 - 成功
 * 400 - 请求参数错误
 * 401 - 未授权
 * 403 - 禁止访问
 * 404 - 资源不存在
 * 500 - 服务器内部错误
 */
@Getter
public enum ResponseCodeEnum {

    SUCCESS(200, "success"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    INVALID_PARAM(400, "参数不合法"),
    PARAM_REQUIRED(400, "参数必填"),

    // 认证错误 401
    UNAUTHORIZED(401, "未授权"),
    TOKEN_EXPIRED(401, "Token 已过期"),
    TOKEN_INVALID(401, "Token 无效"),
    LOGIN_FAILED(401, "登录失败"),

    // 权限错误 403
    FORBIDDEN(403, "禁止访问"),
    PERMISSION_DENIED(403, "权限不足"),

    // 资源错误 404
    NOT_FOUND(404, "资源不存在"),
    USER_NOT_FOUND(404, "用户不存在"),
    SCENIC_NOT_FOUND(404, "景点不存在"),
    DIARY_NOT_FOUND(404, "日记不存在"),

    // 业务错误
    BUSINESS_ERROR(400, "业务错误"),
    DATA_NOT_EXIST(400, "数据不存在"),
    DATA_ALREADY_EXIST(400, "数据已存在"),

    // 服务端错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SYSTEM_ERROR(500, "系统错误");

    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
