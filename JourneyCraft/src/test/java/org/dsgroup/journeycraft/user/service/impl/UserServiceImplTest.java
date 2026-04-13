package org.dsgroup.journeycraft.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.dsgroup.journeycraft.user.entity.User;
import org.dsgroup.journeycraft.user.mapper.UserMapper;
import org.dsgroup.journeycraft.user.vo.reqvo.ChangePasswordReqVO;
import org.dsgroup.journeycraft.user.vo.reqvo.UpdateUserPreferencesReqVO;
import org.dsgroup.journeycraft.user.vo.rspvo.UserPreferencesRspVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    private TokenSessionStore tokenSessionStore;
    private UserServiceImpl userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        tokenSessionStore = new TokenSessionStore();
        objectMapper = new ObjectMapper();
        userService = new UserServiceImpl(userMapper, tokenSessionStore, objectMapper);
    }

    @Test
    void getCurrentUserInfoShouldLoadCurrentUser() throws Exception {
        User user = new User();
        user.setId(1001L);
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setAvatarUrl("avatar.jpg");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setStatus(1);

        UserPreferencesRspVO preferences = new UserPreferencesRspVO();
        preferences.setInterests(List.of("history"));
        user.setPreferences(objectMapper.writeValueAsString(preferences));

        when(userMapper.selectById(1001L)).thenReturn(user);

        String authorization = "Bearer " + tokenSessionStore.issue(1001L, 60, 600).accessToken();

        assertEquals("zhangsan", userService.getCurrentUserInfo(authorization).getUsername());
        assertEquals("history", userService.getCurrentUserPreferences(authorization).getInterests().get(0));
    }

    @Test
    void updateCurrentUserPreferencesShouldMergeExistingValues() throws Exception {
        User user = new User();
        user.setId(1001L);
        user.setUsername("zhangsan");
        user.setStatus(1);

        UserPreferencesRspVO preferences = new UserPreferencesRspVO();
        preferences.setInterests(List.of("history"));
        preferences.setTransportType("walk");
        preferences.setFoodPreferences(List.of("spicy"));
        preferences.setMaxWalkDistance(3000);
        preferences.setBudgetPerDay(200);
        user.setPreferences(objectMapper.writeValueAsString(preferences));

        when(userMapper.selectById(1001L)).thenReturn(user);

        String authorization = "Bearer " + tokenSessionStore.issue(1001L, 60, 600).accessToken();

        UpdateUserPreferencesReqVO reqVO = new UpdateUserPreferencesReqVO();
        reqVO.setBudgetPerDay(500);

        UserPreferencesRspVO result = userService.updateCurrentUserPreferences(authorization, reqVO);
        assertEquals(500, result.getBudgetPerDay());
        assertEquals("walk", result.getTransportType());
        assertEquals("history", result.getInterests().get(0));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertNotNull(captor.getValue().getPreferences());
    }

    @Test
    void changePasswordShouldUpdatePassword() {
        User user = new User();
        user.setId(1001L);
        user.setUsername("zhangsan");
        user.setStatus(1);
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("oldpass"));

        when(userMapper.selectById(1001L)).thenReturn(user);

        String authorization = "Bearer " + tokenSessionStore.issue(1001L, 60, 600).accessToken();

        ChangePasswordReqVO reqVO = new ChangePasswordReqVO();
        reqVO.setOldPassword("oldpass");
        reqVO.setNewPassword("newpass");

        userService.changePassword(authorization, reqVO);
        verify(userMapper).updateById(any(User.class));
    }
}
