package org.dsgroup.journeycraft.user.vo.reqvo;

import lombok.Data;

/**
 * 更新用户信息请求参数。
 */
@Data
public class UpdateUserInfoReqVO {

    private String nickname;

    private String phone;

    private String email;

    private String avatarUrl;
}
