package com.api.auth.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthService {

    /**
     * Generate a random alphanumeric string of a given length
     *
     * @param len The length of the string to generate
     * @return The generated string
     */
    public String alphaNumericString(int len) {
        String AB = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

}
