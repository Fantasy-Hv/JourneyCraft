package org.dsgroup.journeycraft.common.utils;

import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenSessionStore {

    private final ConcurrentMap<String, Session> accessSessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RefreshSession> refreshSessions = new ConcurrentHashMap<>();

    public TokenPair issue(Long userId, long accessExpiresInSeconds, long refreshExpiresInSeconds) {
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        long accessExpiresAt = now + accessExpiresInSeconds * 1000;
        long refreshExpiresAt = now + refreshExpiresInSeconds * 1000;
        accessSessions.put(accessToken, new Session(userId, refreshToken, accessExpiresAt));
        refreshSessions.put(refreshToken, new RefreshSession(userId, refreshExpiresAt));
        return new TokenPair(accessToken, refreshToken, accessExpiresInSeconds);
    }

    public TokenPair refresh(String refreshToken, long accessExpiresInSeconds, long refreshExpiresInSeconds) {
        RefreshSession refreshSession = refreshSessions.get(refreshToken);
        if (refreshSession == null) {
            throw new BusinessException(ResponseCodeEnum.TOKEN_INVALID, "refreshToken无效");
        }
        if (System.currentTimeMillis() > refreshSession.expiresAt) {
            refreshSessions.remove(refreshToken);
            throw new BusinessException(ResponseCodeEnum.TOKEN_EXPIRED, "refreshToken已过期");
        }
        // 新发 token 前尽量清理旧 access token
        accessSessions.entrySet().removeIf(entry -> refreshToken.equals(entry.getValue().refreshToken));
        refreshSessions.remove(refreshToken);
        return issue(refreshSession.userId, accessExpiresInSeconds, refreshExpiresInSeconds);
    }

    public Long requireUserId(String authorization) {
        String accessToken = parseBearerToken(authorization);
        Session session = accessSessions.get(accessToken);
        if (session == null) {
            throw new BusinessException(ResponseCodeEnum.TOKEN_INVALID, "Token无效");
        }
        if (isExpired(session)) {
            accessSessions.remove(accessToken);
            throw new BusinessException(ResponseCodeEnum.TOKEN_EXPIRED, "Token已过期");
        }
        return session.userId;
    }

    public void invalidate(String authorization) {
        String accessToken = parseBearerToken(authorization);
        Session session = accessSessions.remove(accessToken);
        if (session != null) {
            refreshSessions.remove(session.refreshToken);
        }
    }

    private String parseBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "Authorization不能为空");
        }
        if (!authorization.startsWith("Bearer ")) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "Authorization格式错误");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "Token不能为空");
        }
        return token;
    }

    private boolean isExpired(Session session) {
        return System.currentTimeMillis() > session.accessExpiresAt;
    }

    private record Session(Long userId, String refreshToken, long accessExpiresAt) {}

    private record RefreshSession(Long userId, long expiresAt) {}

    public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) {}
}
