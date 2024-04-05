package common.service.session;

public interface SessionManager<T> {
    String createSession(T data);

    T getData(String sessionId);

    boolean validateSession(String sessionId);

    void invalidateSession(String sessionId);
}