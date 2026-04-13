package org.dsgroup.journeycraft.auth.vo.rspvo;

import lombok.Data;

/**
 * 登录成功返回数据。
 */
@Data
public class AuthLoginRspVO {

    /** 访问令牌。 */
    private String token;

    /** 刷新令牌。 */
    private String refreshToken;

    /** 令牌有效期，单位秒。 */
    private Integer expiresIn;

    /** 当前登录用户信息。 */
    private AuthUserRspVO user;
}
