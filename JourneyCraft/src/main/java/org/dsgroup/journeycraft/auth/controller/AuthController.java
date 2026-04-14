package org.dsgroup.journeycraft.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.auth.api.AuthService;
import org.dsgroup.journeycraft.auth.vo.reqvo.LoginReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RefreshTokenReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RegisterReqVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.AuthLoginRspVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.RegisterRspVO;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证模块 HTTP 接口。
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录接口。
     */
    @PostMapping("/login")
    public Response<AuthLoginRspVO> login(@Valid @RequestBody LoginReqVO reqVO) {
        return Response.ok(authService.login(reqVO));
    }

    /**
     * 注册接口。
     */
    @PostMapping("/register")
    public Response<RegisterRspVO> register(@Valid @RequestBody RegisterReqVO reqVO) {
        return Response.ok(authService.register(reqVO));
    }

    /**
     * 登出接口。
     */
    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return Response.ok();
    }

    /**
     * 刷新 token 接口。
     */
    @PostMapping("/refresh")
    public Response<AuthLoginRspVO> refresh(@Valid @RequestBody RefreshTokenReqVO reqVO) {
        return Response.ok(authService.refreshToken(reqVO));
    }
}
