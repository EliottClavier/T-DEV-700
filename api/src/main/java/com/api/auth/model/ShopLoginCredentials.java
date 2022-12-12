package com.api.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ShopLoginCredentials {
    private String name;
    private String password;
}
