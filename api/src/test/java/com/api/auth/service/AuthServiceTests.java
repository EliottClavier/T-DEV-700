package com.api.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTests {

    private final AuthService authService;

    public AuthServiceTests(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @Test
    public void testGetAlphaNumericString() {
        String alphaNumericString = authService.alphaNumericString(10);
        assertEquals(alphaNumericString.length(), 10);
        assertTrue(alphaNumericString.matches("[a-zA-Z0-9]+"));
    }

}
