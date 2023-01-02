package com.api.gateway.tpe.controller;

import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import com.api.auth.service.AuthService;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import com.api.gateway.handler.WebSocketClientTestsHandler;
import com.api.gateway.transaction.model.Message;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeType;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TpeManagerTests {

    private final WebSocketStompClient stompClient;
    private TestRestTemplate testRestTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final TpeRepository tpeRepository;
    private final JWTUtil jwtUtil;
    private final TpeAuthenticationProvider tpeAuthenticationProvider;

    private String tpeToken;
    private StompSession stompSession;
    private final WebSocketClientTestsHandler webSocketClientTestsHandler = new WebSocketClientTestsHandler();


    @Autowired
    public TpeManagerTests(
            WebSocketStompClient stompClient,
            TestRestTemplate testRestTemplate,
            AuthService authService,
            PasswordEncoder passwordEncoder,
            TpeRepository tpeRepository,
            JWTUtil jwtUtil,
            TpeAuthenticationProvider tpeAuthenticationProvider
    ) {
        this.stompClient = stompClient;
        this.testRestTemplate = testRestTemplate;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.tpeRepository = tpeRepository;
        this.jwtUtil = jwtUtil;
        this.tpeAuthenticationProvider = tpeAuthenticationProvider;
    }

    @BeforeAll
    public void beforeAll() {
        // Register a fake TPE to the TPE Manager
        String tpeAndroidId = "AndroidId";
        Optional<Tpe> tpeOptional = tpeRepository.findByAndroidId(tpeAndroidId);
        tpeOptional.ifPresent(tpeRepository::delete);

        String randomPassword = authService.alphaNumericString(12);
        String encodedPassword = passwordEncoder.encode(randomPassword);
        Tpe tpe = new Tpe(tpeAndroidId, encodedPassword);
        tpe.setWhitelisted(true);
        tpeRepository.save(tpe);

        // Get a valid token for the TPE
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(tpeAndroidId, randomPassword);
        tpeAuthenticationProvider.authenticate(authInputToken);
        tpeToken = jwtUtil.generateToken(tpeAndroidId, "TPE Connection");
    }

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + tpeToken);
        headers.add("Connection", "upgrade");
        headers.add("Upgrade", "WebSocket");

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + tpeToken);
        stompSession = stompClient.connect(
                "http://localhost:5080/websocket-manager/secured/tpe/socket",
                headers,
                stompHeaders,
                webSocketClientTestsHandler
        ).get();
    }

//    @AfterEach
//    public void teardown() {
//        stompSession.disconnect();
//    }
//
//    @AfterAll
//    public void afterAll() {
//        // Delete the fake TPE from the TPE Manager
//    }

    @Test
    public void testSynchronizeTpeRedis() throws InterruptedException {
        Thread.sleep(2000);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/synchronize");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        stompSession.send(stompHeaders, jsonMessage);

        Thread.sleep(2000);

        assertEquals(
                "TPE available for transaction.",
                webSocketClientTestsHandler.getMessage().getMessage()
        );

        assertEquals(
                "SYNCHRONIZED",
                webSocketClientTestsHandler.getMessage().getType()
        );
    }

}
