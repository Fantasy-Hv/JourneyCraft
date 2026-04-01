package org.dsgroup.journeycraft.common.exception;

import lombok.Getter;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;

/**
 * 业务异常类
 * <p>
 * 用于处理业务逻辑错误，可携带错误码和详细信息
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMessage());
        this.code = responseCodeEnum.getCode();
        this.message = responseCodeEnum.getMessage();
    }

    public BusinessException(ResponseCodeEnum responseCodeEnum, String message) {
        super(message);
        this.code = responseCodeEnum.getCode();
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(ResponseCodeEnum responseCodeEnum, Throwable cause) {
        super(responseCodeEnum.getMessage(), cause);
        this.code = responseCodeEnum.getCode();
        this.message = responseCodeEnum.getMessage();
    }

}
