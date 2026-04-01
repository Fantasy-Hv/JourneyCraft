package org.dsgroup.journeycraft.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.common.response.Response;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 * <p>
 * 统一处理各类异常，返回标准格式的响应结果
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数错误异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数错误：{}", e.getMessage());
        return Response.error(400, e.getMessage());
    }

    /**
     * 处理认证异常（Token 无效、过期等）
     */
    @ExceptionHandler({BadCredentialsException.class, InsufficientAuthenticationException.class,
            org.springframework.security.authentication.CredentialsExpiredException.class,
            org.springframework.security.authentication.AccountExpiredException.class})
    public Response<Void> handleAuthenticationException(Exception e) {
        log.warn("认证失败：{}", e.getMessage());
        return Response.error(401, "认证失败：" + e.getMessage());
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Response<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return Response.error(403, "权限不足");
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("资源不存在：{}", e.getRequestURL());
        return Response.error(404, "资源不存在");
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Response.error(500, "系统异常：" + e.getMessage());
    }

}
