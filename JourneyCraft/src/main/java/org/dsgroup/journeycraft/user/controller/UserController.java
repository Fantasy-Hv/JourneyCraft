package org.dsgroup.journeycraft.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.user.api.UserService;
import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserInfoReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块 HTTP 接口。
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息。
     */
    @GetMapping("/info")
    public Response<UserInfoRspVO> getCurrentUserInfo(@RequestHeader("Authorization") String authorization) {
        return Response.ok(userService.getCurrentUserInfo(authorization));
    }

    /**
     * 更新当前用户信息。
     */
    @PutMapping("/info")
    public Response<UserInfoRspVO> updateCurrentUserInfo(@RequestHeader("Authorization") String authorization,
                                                         @RequestBody UpdateUserInfoReqVO reqVO) {
        return Response.ok(userService.updateCurrentUserInfo(authorization, reqVO));
    }

    /**
     * 修改当前用户密码。
     */
    @PutMapping("/password")
    public Response<Void> changePassword(@RequestHeader("Authorization") String authorization,
                                         @Valid @RequestBody ChangePasswordReqVO reqVO) {
        userService.changePassword(authorization, reqVO);
        return Response.ok();
    }

    /**
     * 获取当前用户偏好。
     */
    @GetMapping("/preferences")
    public Response<UserPreferencesRspVO> getCurrentUserPreferences(@RequestHeader("Authorization") String authorization) {
        return Response.ok(userService.getCurrentUserPreferences(authorization));
    }

    /**
     * 更新当前用户偏好。
     */
    @PutMapping("/preferences")
    public Response<UserPreferencesRspVO> updateCurrentUserPreferences(@RequestHeader("Authorization") String authorization,
                                                                       @RequestBody UpdateUserPreferencesReqVO reqVO) {
        return Response.ok(userService.updateCurrentUserPreferences(authorization, reqVO));
    }
}
