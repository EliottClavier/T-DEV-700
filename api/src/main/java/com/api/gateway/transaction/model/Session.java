package com.api.gateway.transaction.model;

import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * Session class used to manipulate ones session data (either Shop or TPE)
 */
@Data
@AllArgsConstructor
public class Session implements Serializable {
    @Id
    @NonNull
    private String username;
    @NonNull
    private String sessionId;

    private SessionOrigin origin;

    public Session(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
        this.origin = SessionOrigin.NULL;
    }

    public Session() {
        this.origin = SessionOrigin.NULL;
    }

    public Boolean isValid() {
        return !username.isEmpty() && !sessionId.isEmpty();
    }

    public Boolean isComplete() {
        return username != null && !username.isEmpty() && sessionId != null && !sessionId.isEmpty();
    }
}
