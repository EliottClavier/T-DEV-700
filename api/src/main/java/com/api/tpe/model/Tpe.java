package com.api.tpe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "TPE")
public class Tpe implements Serializable {

    @Id
    // Id = Adresse MAC
    public String id;
    public String ip;

    public Boolean isValid() {
        return !id.isEmpty() && !ip.isEmpty();
    }

}
