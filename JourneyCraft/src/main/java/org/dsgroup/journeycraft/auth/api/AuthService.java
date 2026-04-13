package org.dsgroup.journeycraft.auth.api;

import org.dsgroup.journeycraft.auth.vo.reqvo.LoginReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RefreshTokenReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RegisterReqVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.AuthLoginRspVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.RegisterRspVO;

/**
 * 认证模块对外服务接口。
 */
public interface AuthService {

    /**
     * 用户登录并返回 token 信息。
     */
    AuthLoginRspVO login(LoginReqVO reqVO);

    /**
     * 注册新用户。
     */
    RegisterRspVO register(RegisterReqVO reqVO);

    /**
     * 用户登出并使当前 token 失效。
     */
    void logout(String authorization);

    /**
     * 使用 refreshToken 刷新 accessToken。
     */
    AuthLoginRspVO refreshToken(RefreshTokenReqVO reqVO);
}
