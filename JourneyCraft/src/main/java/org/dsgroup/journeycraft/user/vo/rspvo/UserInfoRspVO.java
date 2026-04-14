package org.dsgroup.journeycraft.user.vo.rspvo;

import lombok.Data;

/**
 * 当前用户信息返回数据。
 */
@Data
public class UserInfoRspVO {

    /** 用户主键。 */
    private Long id;

    /** 用户名。 */
    private String username;

    /** 昵称。 */
    private String nickname;

    /** 头像地址。 */
    private String avatarUrl;

    /** 手机号。 */
    private String phone;

    /** 邮箱。 */
    private String email;

    /** 用户偏好。 */
    private UserPreferencesRspVO preferences;

    /** 用户状态。 */
    private Integer status;
}
