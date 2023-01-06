package com.api.auth.controller;

import com.api.auth.model.ManagerLoginCredentials;
import com.api.auth.security.JWTUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthManagerControllerTests {

    private final RestTemplate restTemplate;
    private final JWTUtil jwtUtil;

    @Value("${default.manager.username}")
    private String username;

    @Value("${default.manager.password}")
    private String password;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public AuthManagerControllerTests(
            RestTemplate restTemplate,
            JWTUtil jwtUtil
    ) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Test
    public void testLogin() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/manager/login",
                new ManagerLoginCredentials(username, password),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the token and correct HTTP Status
        assertTrue(responseJson.has("token"));
        assertNotNull(responseJson.get("token"));
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        // Check if the token is valid
        String token = responseJson.get("token").toString();
        assertEquals(jwtUtil.getSubjectFromToken(token), "Manager Connection");
        assertEquals(jwtUtil.validateTokenAndRetrieveSubject(token, "Manager Connection"), username);

    }

    @Test
    public void testLoginBadCredentials() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/manager/login",
                new ManagerLoginCredentials(username, "wrongPassword"),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the bad credentials message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "Bad credentials");
        assertEquals(response.getStatusCodeValue(), HttpStatus.UNAUTHORIZED.value());

    }

}
