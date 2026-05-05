package org.dsgroup.journeycraft.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private UserServiceImpl userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userService = new UserServiceImpl(userMapper, objectMapper);
    }

    @Test
    void getUserInfoShouldLoadUserById() throws Exception {
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

        assertEquals("zhangsan", userService.getUserInfo(1001L).getUsername());
        assertEquals("history", userService.getUserPreferences(1001L).getInterests().get(0));
        assertEquals("zhangsan", userService.getUserById(1001L).getUsername());
        assertEquals("张三", userService.getUserNickname(1001L));
        assertEquals("avatar.jpg", userService.getUserAvatarUrl(1001L));
    }

    @Test
    void updateUserPreferencesShouldMergeExistingValues() throws Exception {
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

        UpdateUserPreferencesReqVO reqVO = new UpdateUserPreferencesReqVO();
        reqVO.setBudgetPerDay(500);

        UserPreferencesRspVO result = userService.updateUserPreferences(1001L, reqVO);
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

        ChangePasswordReqVO reqVO = new ChangePasswordReqVO();
        reqVO.setOldPassword("oldpass");
        reqVO.setNewPassword("newpass");

        userService.changePassword(1001L, reqVO);
        verify(userMapper).updateById(any(User.class));
    }
}
