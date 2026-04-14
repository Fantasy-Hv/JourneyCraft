package org.dsgroup.journeycraft.auth.service.impl;

import org.dsgroup.journeycraft.auth.api.AuthService;
import org.dsgroup.journeycraft.auth.vo.reqvo.LoginReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RefreshTokenReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RegisterReqVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.AuthLoginRspVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.AuthUserRspVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.RegisterRspVO;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.user.entity.User;
import org.dsgroup.journeycraft.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * 认证服务实现，负责注册、登录、登出和 token 刷新。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TokenSessionStore tokenSessionStore;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiresInMillis;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiresInMillis;

    /**
     * 登录校验并签发 token。
     */
    @Override
    public AuthLoginRspVO login(LoginReqVO reqVO) {
        User user = userMapper.selectOne(lambdaQuery(User.class)
                .and(w -> w.eq(User::getUsername, reqVO.getUsername())
                        .or()
                        .eq(User::getPhone, reqVO.getUsername())
                        .or()
                        .eq(User::getEmail, reqVO.getUsername()))
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(reqVO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCodeEnum.LOGIN_FAILED, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN, "账号已禁用");
        }
        return buildTokenResponse(user);
    }

    /**
     * 创建用户账号。
     */
    @Override
    public RegisterRspVO register(RegisterReqVO reqVO) {
        if (userMapper.selectCount(lambdaQuery(User.class).eq(User::getUsername, reqVO.getUsername())) > 0) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "用户名已存在");
        }
        if (reqVO.getPhone() != null && !reqVO.getPhone().isBlank()
                && userMapper.selectCount(lambdaQuery(User.class).eq(User::getPhone, reqVO.getPhone())) > 0) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "手机号已存在");
        }
        if (reqVO.getEmail() != null && !reqVO.getEmail().isBlank()
                && userMapper.selectCount(lambdaQuery(User.class).eq(User::getEmail, reqVO.getEmail())) > 0) {
            throw new BusinessException(ResponseCodeEnum.DATA_ALREADY_EXIST, "邮箱已存在");
        }
        RegisterRspVO rspVO = new RegisterRspVO();
        User user = new User();
        user.setUsername(reqVO.getUsername());
        user.setPassword(passwordEncoder.encode(reqVO.getPassword()));
        user.setNickname(reqVO.getNickname() == null || reqVO.getNickname().isBlank() ? reqVO.getUsername() : reqVO.getNickname());
        user.setPhone(reqVO.getPhone());
        user.setEmail(reqVO.getEmail());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        rspVO.setUserId(user.getId());
        return rspVO;
    }

    /**
     * 注销当前登录态。
     */
    @Override
    public void logout(String authorization) {
        tokenSessionStore.invalidate(authorization);
    }

    /**
     * 根据 refreshToken 重新签发 token。
     */
    @Override
    public AuthLoginRspVO refreshToken(RefreshTokenReqVO reqVO) {
        TokenSessionStore.TokenPair tokenPair = tokenSessionStore.refresh(
                reqVO.getRefreshToken(),
                tokenExpiresInMillis / 1000,
                refreshTokenExpiresInMillis / 1000
        );
        Long userId = tokenSessionStore.requireUserId("Bearer " + tokenPair.accessToken());
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_FOUND, "用户不存在");
        }
        return buildTokenResponse(user, tokenPair);
    }

    /**
     * 按默认过期时间生成 token 响应。
     */
    private AuthLoginRspVO buildTokenResponse(User user) {
        TokenSessionStore.TokenPair tokenPair = tokenSessionStore.issue(
                user.getId(),
                tokenExpiresInMillis / 1000,
                refreshTokenExpiresInMillis / 1000
        );
        return buildTokenResponse(user, tokenPair);
    }

    /**
     * 组装登录响应对象。
     */
    private AuthLoginRspVO buildTokenResponse(User user, TokenSessionStore.TokenPair tokenPair) {
        AuthUserRspVO userRspVO = new AuthUserRspVO();
        userRspVO.setId(user.getId());
        userRspVO.setUsername(user.getUsername());
        userRspVO.setNickname(user.getNickname());
        userRspVO.setAvatarUrl(user.getAvatarUrl());

        AuthLoginRspVO rspVO = new AuthLoginRspVO();
        rspVO.setToken(tokenPair.accessToken());
        rspVO.setRefreshToken(tokenPair.refreshToken());
        rspVO.setExpiresIn((int) tokenPair.expiresInSeconds());
        rspVO.setUser(userRspVO);
        return rspVO;
    }
}
