package shared.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private String sessionId;
    private Map<String, Object> attributes;
    private long creationTime;
    private long expirationTime;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
        this.attributes = new HashMap<>();
        this.creationTime = System.currentTimeMillis();
        this.expirationTime = this.creationTime + getSessionTimeout();
    }

    public String getSessionId() {
        return sessionId;
    }

    public <T> void setAttribute(String key, T value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz) {
        return (T) attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void updateExpirationTime() {
        this.expirationTime = System.currentTimeMillis() + getSessionTimeout();
    }

    private long getSessionTimeout() {
        return 1 * 24 * 60 * 60 * 1000; // 1 days in milliseconds
    }

    public boolean isValid() {
        return System.currentTimeMillis() <= expirationTime;
    }
}
