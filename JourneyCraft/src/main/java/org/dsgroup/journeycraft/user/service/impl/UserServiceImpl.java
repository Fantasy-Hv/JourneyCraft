package org.dsgroup.journeycraft.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.dsgroup.journeycraft.user.api.UserApi;
import org.dsgroup.journeycraft.user.entity.User;
import org.dsgroup.journeycraft.user.mapper.UserMapper;
import org.dsgroup.journeycraft.user.service.UserService;
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
public class UserServiceImpl implements UserService, UserApi {

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据 userId 查询用户信息。
     */
    @Override
    public UserInfoRspVO getUserInfo(Long userId) {
        User user = requireUser(userId);
        return toUserInfoRsp(user);
    }

    /**
     * 根据 userId 更新用户基础资料。
     */
    @Override
    public UserInfoRspVO updateUserInfo(Long userId, UpdateUserInfoReqVO reqVO) {
        User user = requireUser(userId);
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
     * 根据 userId 修改用户密码。
     */
    @Override
    public void changePassword(Long userId, ChangePasswordReqVO reqVO) {
        User user = requireUser(userId);
        if (!passwordEncoder.matches(reqVO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(reqVO.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 根据 userId 获取用户偏好设置。
     */
    @Override
    public UserPreferencesRspVO getUserPreferences(Long userId) {
        User user = requireUser(userId);
        return parsePreferences(user.getPreferences());
    }

    /**
     * 根据 userId 按字段合并更新用户偏好设置。
     */
    @Override
    public UserPreferencesRspVO updateUserPreferences(Long userId, UpdateUserPreferencesReqVO reqVO) {
        User user = requireUser(userId);
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
     * 根据 userId 加载用户。
     */
    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    /**
     * 对外接口：根据 userId 获取用户信息。
     */
    @Override
    public UserInfoRspVO getUserById(Long userId) {
        return getUserInfo(userId);
    }

    /**
     * 对外接口：根据 userId 获取昵称。
     */
    @Override
    public String getUserNickname(Long userId) {
        return requireUser(userId).getNickname();
    }

    /**
     * 对外接口：根据 userId 获取头像地址。
     */
    @Override
    public String getUserAvatarUrl(Long userId) {
        return requireUser(userId).getAvatarUrl();
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
