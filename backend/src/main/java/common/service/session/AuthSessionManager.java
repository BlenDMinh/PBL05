package common.service.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import modules.auth.dto.UserPasswordDto;

public class AuthSessionManager implements SessionManager<UserPasswordDto> {
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes
    private ConcurrentHashMap<String, Session<UserPasswordDto>> sessions = new ConcurrentHashMap<>();
    private static AuthSessionManager instance;

    public static AuthSessionManager getInstance() {
        return instance;
    }

    private AuthSessionManager() {
    }

    static {
        instance = new AuthSessionManager();
    }

    @Override
    public String createSession(UserPasswordDto data) {
        String sessionId = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + SESSION_TIMEOUT_MS;
        Session<UserPasswordDto> session = new Session<>(sessionId, data, currentTime, expirationTime);
        sessions.put(sessionId, session);
        return sessionId;
    }

    @Override
    public UserPasswordDto getData(String sessionId) {
        Session<UserPasswordDto> session = sessions.get(sessionId);
        return session != null ? session.getData() : null;
    }

    @Override
    public boolean validateSession(String sessionId) {
        Session<UserPasswordDto> session = sessions.get(sessionId);
        if (session != null && session.getExpirationTime() > System.currentTimeMillis()) {
            // Extend session expiration time
            session.setExpirationTime(System.currentTimeMillis() + SESSION_TIMEOUT_MS);
            return true;
        }
        return false;
    }

    @Override
    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }

}
