package org.dsgroup.journeycraft.auth.vo.rspvo;

import lombok.Data;

/**
 * 登录响应中的用户信息。
 */
@Data
public class AuthUserRspVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatarUrl;
}
