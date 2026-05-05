package org.dsgroup.journeycraft.user.service;

import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserInfoReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;

/**
 * 用户模块内部业务接口，供 controller 调用。
 */
public interface UserService {

    /**
     * 根据用户 ID 获取用户信息。
     */
    UserInfoRspVO getUserInfo(Long userId);

    /**
     * 根据用户 ID 更新基础资料。
     */
    UserInfoRspVO updateUserInfo(Long userId, UpdateUserInfoReqVO reqVO);

    /**
     * 根据用户 ID 修改密码。
     */
    void changePassword(Long userId, ChangePasswordReqVO reqVO);

    /**
     * 根据用户 ID 获取偏好设置。
     */
    UserPreferencesRspVO getUserPreferences(Long userId);

    /**
     * 根据用户 ID 更新偏好设置。
     */
    UserPreferencesRspVO updateUserPreferences(Long userId, UpdateUserPreferencesReqVO reqVO);
}
