package org.dsgroup.journeycraft.user.vo.rspvo;

import lombok.Data;

/**
 * 用户信息响应数据。
 */
@Data
public class UserInfoRspVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatarUrl;

    private String phone;

    private String email;

    private UserPreferencesRspVO preferences;

    private Integer status;
}
