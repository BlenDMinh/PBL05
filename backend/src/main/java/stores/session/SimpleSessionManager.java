package stores.session;

import java.util.HashMap;
import java.util.Map;

public class SimpleSessionManager implements SessionManager {
    private static volatile SimpleSessionManager instance;

    public static SimpleSessionManager getInstance() {
        return instance;
    }

    static {
        instance = new SimpleSessionManager();
    }
    private Map<String, Session> sessionMap;

    private SimpleSessionManager() {
        this.sessionMap = new HashMap<>();
    }

    @Override
    public Session createSession() {
        Session session = new Session();
        sessionMap.put(session.getSessionId(), session);
        return session;
    }

    @Override
    public Session getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    @Override
    public void invalidateSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    @Override
    public void removeExpiredSessions() {
        for (String sessionId : sessionMap.keySet()) {
            Session session = sessionMap.get(sessionId);
            if (!session.isValid()) {
                this.invalidateSession(sessionId);
            }
        }
    }
}
