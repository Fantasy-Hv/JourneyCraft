package org.dsgroup.journeycraft.auth.vo.rspvo;

import lombok.Data;

/**
 * 登录成功响应数据。
 */
@Data
public class AuthLoginRspVO {

    private String token;

    private String refreshToken;

    private Integer expiresIn;

    private AuthUserRspVO user;
}
