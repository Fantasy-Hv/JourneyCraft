package org.dsgroup.journeycraft.auth.vo.rspvo;

import lombok.Data;

/**
 * 登录响应里的用户摘要信息。
 */
@Data
public class AuthUserRspVO {

    /** 用户主键。 */
    private Long id;

    /** 用户名。 */
    private String username;

    /** 昵称。 */
    private String nickname;

    /** 头像地址。 */
    private String avatarUrl;
}
