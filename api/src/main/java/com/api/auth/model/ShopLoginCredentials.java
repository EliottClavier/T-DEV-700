package com.api.auth.model;

import lombok.*;

/**
 * Shop login credentials model
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ShopLoginCredentials {
    private String name;
    private String password;
}
