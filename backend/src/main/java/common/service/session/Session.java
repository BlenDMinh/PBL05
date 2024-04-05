package common.service.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Session<T> {
    private String sessionId;
    private T data;
    private long creationTime;
    private long expirationTime;
}
