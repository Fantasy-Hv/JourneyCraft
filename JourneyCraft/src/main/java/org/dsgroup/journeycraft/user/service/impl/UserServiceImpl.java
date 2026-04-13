package org.dsgroup.journeycraft.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.dsgroup.journeycraft.user.api.UserService;
import org.dsgroup.journeycraft.user.entity.User;
import org.dsgroup.journeycraft.user.mapper.UserMapper;
import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserInfoReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserInfoRspVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现，负责用户信息与偏好管理。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final TokenSessionStore tokenSessionStore;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 查询当前用户信息。
     */
    @Override
    public UserInfoRspVO getCurrentUserInfo(String authorization) {
        User user = requireCurrentUser(authorization);
        return toUserInfoRsp(user);
    }

    /**
     * 更新当前用户基础资料。
     */
    @Override
    public UserInfoRspVO updateCurrentUserInfo(String authorization, UpdateUserInfoReqVO reqVO) {
        User user = requireCurrentUser(authorization);
        if (reqVO.getNickname() != null) {
            user.setNickname(reqVO.getNickname());
        }
        if (reqVO.getPhone() != null) {
            user.setPhone(reqVO.getPhone());
        }
        if (reqVO.getEmail() != null) {
            user.setEmail(reqVO.getEmail());
        }
        if (reqVO.getAvatarUrl() != null) {
            user.setAvatarUrl(reqVO.getAvatarUrl());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return toUserInfoRsp(userMapper.selectById(user.getId()));
    }

    /**
     * 修改当前用户密码。
     */
    @Override
    public void changePassword(String authorization, ChangePasswordReqVO reqVO) {
        User user = requireCurrentUser(authorization);
        if (!passwordEncoder.matches(reqVO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(reqVO.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 获取当前用户偏好设置。
     */
    @Override
    public UserPreferencesRspVO getCurrentUserPreferences(String authorization) {
        User user = requireCurrentUser(authorization);
        return parsePreferences(user.getPreferences());
    }

    /**
     * 按字段合并更新用户偏好设置。
     */
    @Override
    public UserPreferencesRspVO updateCurrentUserPreferences(String authorization, UpdateUserPreferencesReqVO reqVO) {
        User user = requireCurrentUser(authorization);
        UserPreferencesRspVO preferences = parsePreferences(user.getPreferences());
        if (reqVO.getInterests() != null) {
            preferences.setInterests(reqVO.getInterests());
        }
        if (reqVO.getTransportType() != null) {
            preferences.setTransportType(reqVO.getTransportType());
        }
        if (reqVO.getFoodPreferences() != null) {
            preferences.setFoodPreferences(reqVO.getFoodPreferences());
        }
        if (reqVO.getMaxWalkDistance() != null) {
            preferences.setMaxWalkDistance(reqVO.getMaxWalkDistance());
        }
        if (reqVO.getBudgetPerDay() != null) {
            preferences.setBudgetPerDay(reqVO.getBudgetPerDay());
        }
        user.setPreferences(writeJson(preferences));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return preferences;
    }

    /**
     * 解析 token 并加载当前用户。
     */
    private User requireCurrentUser(String authorization) {
        Long userId = tokenSessionStore.requireUserId(authorization);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    /**
     * 实体转用户信息响应。
     */
    private UserInfoRspVO toUserInfoRsp(User user) {
        UserInfoRspVO rspVO = new UserInfoRspVO();
        rspVO.setId(user.getId());
        rspVO.setUsername(user.getUsername());
        rspVO.setNickname(user.getNickname());
        rspVO.setAvatarUrl(user.getAvatarUrl());
        rspVO.setPhone(user.getPhone());
        rspVO.setEmail(user.getEmail());
        rspVO.setStatus(user.getStatus());
        rspVO.setPreferences(parsePreferences(user.getPreferences()));
        return rspVO;
    }

    /**
     * 解析用户偏好 JSON。
     */
    private UserPreferencesRspVO parsePreferences(String json) {
        if (json == null || json.isBlank()) {
            return new UserPreferencesRspVO();
        }
        try {
            return objectMapper.readValue(json, UserPreferencesRspVO.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "偏好设置解析失败");
        }
    }

    /**
     * 序列化用户偏好为 JSON。
     */
    private String writeJson(UserPreferencesRspVO preferences) {
        try {
            return objectMapper.writeValueAsString(preferences);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "偏好设置序列化失败");
        }
    }
}
