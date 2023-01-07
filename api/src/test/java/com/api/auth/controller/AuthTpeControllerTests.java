package com.api.auth.controller;

import com.api.auth.security.JWTUtil;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthTpeControllerTests {

    private final RestTemplate restTemplate;
    private final JWTUtil jwtUtil;
    private final TpeRepository tpeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${tpe.register.secret.header}")
    private String secretHeader;

    @Value("${tpe.register.secret.key}")
    private String secretKey;

    private final String androidId = "androidId";
    private final String password = "password";

    private final String androidIdNewTpe = "androidIdNewTpe";
    private final String passwordNewTpe = "passwordNewTpe";

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public AuthTpeControllerTests(
            RestTemplate restTemplate,
            JWTUtil jwtUtil,
            TpeRepository tpeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.tpeRepository = tpeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Change whitelist status of a TPE
     *
     * @param whitelisted whitelist status
     */
    private void setTpeWhitelistedStatus(String androidId, Boolean whitelisted) {
        // Change whitelist status of the TPE find by androidId
        Optional<Tpe> tpeOptional = tpeRepository.findByAndroidId(androidId);
        tpeOptional.ifPresent(tpe -> {
            tpe.setWhitelisted(whitelisted);
            tpeRepository.save(tpe);
        });
    }

    @BeforeAll
    public void setup() {
        tpeRepository.findByAndroidId(androidId).ifPresent(tpeRepository::delete);
    }

    @BeforeEach
    public void setUp() {
        // Create new TPE
        Tpe tpe = new Tpe(androidId, passwordEncoder.encode(password));
        tpe.setWhitelisted(true);
        tpeRepository.save(tpe);
    }

    @AfterEach
    public void tearDown() {
        // Delete new TPE
        if (tpeRepository.existsByAndroidId(androidId)) {
            tpeRepository.deleteByAndroidId(androidId);
        }

        if (tpeRepository.existsByAndroidId(androidIdNewTpe)) {
            tpeRepository.deleteByAndroidId(androidIdNewTpe);
        }
    }

    @Test
    public void testLogin() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/login",
                new Tpe(androidId, password),
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
        assertEquals(jwtUtil.getSubjectFromToken(token), "TPE Connection");
        assertEquals(jwtUtil.validateTokenAndRetrieveSubject(token, "TPE Connection"), androidId);

    }

    @Test
    public void testLoginBadCredentials() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/login",
                new Tpe(androidId, "badPassword"),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the bad credentials message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "Bad credentials");
        assertEquals(response.getStatusCodeValue(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void testLoginNotWhitelisted() throws JSONException {
        // Blacklist the TPE
        setTpeWhitelistedStatus(androidId, false);

        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/login",
                new Tpe(androidId, password),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the not whitelisted message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "Not whitelisted.");
        assertEquals(response.getStatusCodeValue(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void testRegisterTpe() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(secretHeader, secretKey);

        // Post for entity with headers and new TPE as body
        HttpEntity<Tpe> requestEntity = new HttpEntity<>(new Tpe(androidIdNewTpe, passwordNewTpe), headers);

        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/register",
                requestEntity,
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the bad credentials message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "TPE registered successfully. It needs to be whitelisted.");
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        // Request to check if TPE is correctly registered without being whitelisted
        ResponseEntity responseLogin = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/login",
                new Tpe(androidIdNewTpe, passwordNewTpe),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseLoginJson = new JSONObject(responseLogin.getBody().toString());

        // Check if the response contains the not whitelisted message and correct HTTP Status
        assertTrue(responseLoginJson.has("message"));
        assertEquals(responseLoginJson.get("message"), "Not whitelisted.");
        assertEquals(responseLogin.getStatusCodeValue(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void testAlreadyRegisteredTpe() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(secretHeader, secretKey);

        // Post for entity with headers and new TPE as body
        HttpEntity<Tpe> requestEntity = new HttpEntity<>(new Tpe(androidId, password), headers);

        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/register",
                requestEntity,
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the already registered message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "TPE already registered.");
        assertEquals(response.getStatusCodeValue(), HttpStatus.CONFLICT.value());
    }

    @Test
    public void testRegisterTpeWithoutSecretHeader() {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/tpe/register",
                new Tpe(androidId, password),
                String.class
        );

        // Check if the response contains correct HTTP Status
        assertEquals(response.getStatusCodeValue(), HttpStatus.UNAUTHORIZED.value());
    }

}
