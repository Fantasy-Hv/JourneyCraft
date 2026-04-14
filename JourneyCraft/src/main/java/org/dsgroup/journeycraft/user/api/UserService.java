package org.dsgroup.journeycraft.user.api;

import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserInfoReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;

/**
 * 用户模块对外服务接口。
 */
public interface UserService {

    /**
     * 获取当前登录用户信息。
     */
    UserInfoRspVO getCurrentUserInfo(String authorization);

    /**
     * 更新当前登录用户基础资料。
     */
    UserInfoRspVO updateCurrentUserInfo(String authorization, UpdateUserInfoReqVO reqVO);

    /**
     * 修改当前登录用户密码。
     */
    void changePassword(String authorization, ChangePasswordReqVO reqVO);

    /**
     * 获取当前登录用户偏好设置。
     */
    UserPreferencesRspVO getCurrentUserPreferences(String authorization);

    /**
     * 更新当前登录用户偏好设置。
     */
    UserPreferencesRspVO updateCurrentUserPreferences(String authorization, UpdateUserPreferencesReqVO reqVO);
}
