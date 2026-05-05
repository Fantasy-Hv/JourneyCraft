package org.dsgroup.journeycraft.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.user.service.UserService;
import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserInfoReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块 HTTP 接口。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据 userId 获取用户信息。
     */
    @GetMapping("/info")
    public Response<UserInfoRspVO> getUserInfo(@RequestParam Long userId) {
        return Response.ok(userService.getUserInfo(userId));
    }

    /**
     * 根据 userId 更新用户信息。
     */
    @PutMapping("/info")
    public Response<UserInfoRspVO> updateUserInfo(@RequestParam Long userId,
                                                  @RequestBody UpdateUserInfoReqVO reqVO) {
        return Response.ok(userService.updateUserInfo(userId, reqVO));
    }

    /**
     * 根据 userId 修改用户密码。
     */
    @PutMapping("/password")
    public Response<Void> changePassword(@RequestParam Long userId,
                                         @Valid @RequestBody ChangePasswordReqVO reqVO) {
        userService.changePassword(userId, reqVO);
        return Response.ok();
    }

    /**
     * 根据 userId 获取用户偏好。
     */
    @GetMapping("/preferences")
    public Response<UserPreferencesRspVO> getUserPreferences(@RequestParam Long userId) {
        return Response.ok(userService.getUserPreferences(userId));
    }

    /**
     * 根据 userId 更新用户偏好。
     */
    @PutMapping("/preferences")
    public Response<UserPreferencesRspVO> updateUserPreferences(@RequestParam Long userId,
                                                                @RequestBody UpdateUserPreferencesReqVO reqVO) {
        return Response.ok(userService.updateUserPreferences(userId, reqVO));
    }
}
