package org.dsgroup.journeycraft.user.api;

import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;

/**
 * 用户模块对外接口，供其他模块跨模块调用。
 */
public interface UserApi {

    /**
     * 根据用户 ID 获取用户信息。
     */
    UserInfoRspVO getUserById(Long userId);

    /**
     * 根据用户 ID 获取昵称。
     */
    String getUserNickname(Long userId);

    /**
     * 根据用户 ID 获取头像地址。
     */
    String getUserAvatarUrl(Long userId);
}
