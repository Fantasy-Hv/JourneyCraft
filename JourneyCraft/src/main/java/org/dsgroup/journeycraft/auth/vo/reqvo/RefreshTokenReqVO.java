package org.dsgroup.journeycraft.auth.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 token 请求参数。
 */
@Data
public class RefreshTokenReqVO {

    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
