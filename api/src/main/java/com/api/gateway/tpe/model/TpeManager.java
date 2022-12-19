package com.api.gateway.tpe.model;

import com.api.gateway.transaction.model.Session;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "TPE")
public class TpeManager extends Session {
    public TpeManager(String username, String sessionId) {
        super(username, sessionId);
    }
}
