package com.api.auth.model;

import lombok.*;

/**
 * Manager login credentials model
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ManagerLoginCredentials {
    private String username;
    private String password;
}
