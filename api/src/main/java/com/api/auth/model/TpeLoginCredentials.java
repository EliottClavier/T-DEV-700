package com.api.auth.model;

import lombok.*;

/**
 * Tpe login credentials model
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class TpeLoginCredentials {
    private String androidId;
    private String password;
}
