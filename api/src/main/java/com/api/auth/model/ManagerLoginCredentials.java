package com.api.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ManagerLoginCredentials {
    private String username;
    private String password;
}
