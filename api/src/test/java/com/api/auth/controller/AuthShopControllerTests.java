package com.api.auth.controller;

import com.api.auth.model.ManagerLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import com.api.bank.service.ShopService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthShopControllerTests {

    private final RestTemplate restTemplate;
    private final JWTUtil jwtUtil;
    private final ShopService shopService;
    private final ShopRepository shopRepository;

    @Value("${default.shop.username}")
    private String username;

    @Value("${default.shop.password}")
    private String password;

    private final String usernameNewShop = "newShop";
    private final String passwordNewShop = "newShopPassword";

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public AuthShopControllerTests(
            RestTemplate restTemplate,
            JWTUtil jwtUtil,
            ShopRepository shopRepository,
            ShopService shopService
    ) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.shopRepository = shopRepository;
        this.shopService = shopService;
    }

    /**
     * Change whitelist status of the default shop
     *
     * @param whitelisted whitelist status
     */
    private void setShopWhitelistedStatus(Boolean whitelisted) {
        // Change whitelist status of the shop find by shopName
        Optional<Shop> shopOptional = shopRepository.findByName(username);
        shopOptional.ifPresent(shop -> {
            shop.setWhitelisted(whitelisted);
            shopRepository.save(shop);
        });
    }

    @BeforeEach
    public void setUp() {
        // Set shop whitelisted status to true
        setShopWhitelistedStatus(true);
    }

    @AfterEach
    public void tearDown() {
        // Delete new shop
        shopRepository.findByName(usernameNewShop).ifPresent(shop ->
                shopService.deleteShopStackByUUID(shop.getId().toString())
        );
    }

    @Test
    public void testLogin() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/login",
                new Shop(username, password),
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
        assertEquals(jwtUtil.getSubjectFromToken(token), "Shop Connection");
        assertEquals(jwtUtil.validateTokenAndRetrieveSubject(token, "Shop Connection"), username);

    }

    @Test
    public void testLoginBadCredentials() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/login",
                new Shop(username, "wrongPassword"),
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
        // Blacklist the shop
        setShopWhitelistedStatus(false);

        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/login",
                new Shop(username, password),
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
    public void testRegisterShop() throws JSONException, InterruptedException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/register",
                new Shop(usernameNewShop, passwordNewShop),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the bad credentials message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "Shop registered successfully. It needs to be whitelisted.");
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        Thread.sleep(1000);

        // Request to check if Shop is correctly registered without being whitelisted
        ResponseEntity responseLogin = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/login",
                new Shop(usernameNewShop, passwordNewShop),
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
    public void testAlreadyRegisteredShop() throws JSONException {
        // Request
        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:" + serverPort + "/auth/shop/register",
                new Shop(username, password),
                String.class
        );

        // Convert the response to a JSON object
        JSONObject responseJson = new JSONObject(response.getBody().toString());

        // Check if the response contains the already registered message and correct HTTP Status
        assertTrue(responseJson.has("message"));
        assertEquals(responseJson.get("message"), "Shop already registered.");
        assertEquals(response.getStatusCodeValue(), HttpStatus.CONFLICT.value());
    }

}
