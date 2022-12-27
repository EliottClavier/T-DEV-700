package com.api.gateway.tpe.model;

import com.api.gateway.transaction.model.Session;
import lombok.*;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "TPE")
public class TpeManager extends Session {

    public TpeManager(String username, String sessionId) {
        super(username, sessionId);
    }

    @Id
    @AccessType(AccessType.Type.PROPERTY)
    public String getId() {
        return this.getUsername();
    }
}
