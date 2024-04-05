package stores.session;

public interface SessionManager {
    Session createSession();

    Session getSession(String sessionId);

    void invalidateSession(String sessionId);

    void removeExpiredSessions();
}