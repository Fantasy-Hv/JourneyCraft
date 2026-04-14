package org.dsgroup.journeycraft.auth.service.impl;

import org.dsgroup.journeycraft.auth.vo.reqvo.LoginReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RefreshTokenReqVO;
import org.dsgroup.journeycraft.auth.vo.reqvo.RegisterReqVO;
import org.dsgroup.journeycraft.auth.vo.rspvo.AuthLoginRspVO;
import org.dsgroup.journeycraft.common.utils.TokenSessionStore;
import org.dsgroup.journeycraft.user.entity.User;
import org.dsgroup.journeycraft.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    private TokenSessionStore tokenSessionStore;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        tokenSessionStore = new TokenSessionStore();
        authService = new AuthServiceImpl(userMapper, tokenSessionStore);
        ReflectionTestUtils.setField(authService, "tokenExpiresInMillis", 60000L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpiresInMillis", 600000L);
    }

    @Test
    void registerShouldReturnNewUserId() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1001L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        RegisterReqVO reqVO = new RegisterReqVO();
        reqVO.setUsername("zhangsan");
        reqVO.setPassword("password123");
        reqVO.setNickname("张三");

        assertEquals(1001L, authService.register(reqVO).getUserId());
    }

    @Test
    void loginShouldReturnTokenPair() {
        User user = new User();
        user.setId(1001L);
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setAvatarUrl("avatar.jpg");
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123"));
        user.setStatus(1);

        when(userMapper.selectOne(any())).thenReturn(user);

        LoginReqVO reqVO = new LoginReqVO();
        reqVO.setUsername("zhangsan");
        reqVO.setPassword("password123");

        AuthLoginRspVO rspVO = authService.login(reqVO);
        assertNotNull(rspVO.getToken());
        assertNotNull(rspVO.getRefreshToken());
        assertEquals(1001L, rspVO.getUser().getId());
        assertEquals("张三", rspVO.getUser().getNickname());
    }

    @Test
    void refreshShouldIssueNewTokens() {
        User user = new User();
        user.setId(1001L);
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setAvatarUrl("avatar.jpg");
        user.setStatus(1);
        when(userMapper.selectById(1001L)).thenReturn(user);

        TokenSessionStore.TokenPair pair = tokenSessionStore.issue(1001L, 60, 600);
        RefreshTokenReqVO reqVO = new RefreshTokenReqVO();
        reqVO.setRefreshToken(pair.refreshToken());

        AuthLoginRspVO rspVO = authService.refreshToken(reqVO);
        assertNotNull(rspVO.getToken());
        assertNotNull(rspVO.getRefreshToken());
        assertEquals(1001L, rspVO.getUser().getId());

        verify(userMapper).selectById(1001L);
    }
}
