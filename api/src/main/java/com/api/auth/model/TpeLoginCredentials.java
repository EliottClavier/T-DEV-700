package com.api.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TpeLoginCredentials {
    private String mac;
    private String serial;
}
