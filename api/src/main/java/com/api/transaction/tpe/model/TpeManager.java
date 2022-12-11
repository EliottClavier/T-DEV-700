package com.api.transaction.tpe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "TPE")
public class TpeManager implements Serializable {

    @Id
    // Id = Adresse MAC
    public String id;
    public String ip;

    public Boolean isValid() {
        return id != null && !id.isEmpty() && ip != null && !ip.isEmpty();
    }

}
