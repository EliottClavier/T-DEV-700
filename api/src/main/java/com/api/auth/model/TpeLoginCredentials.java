package com.api.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TpeLoginCredentials {
    private String androidId;
    private String password;
}
