package com.api.gateway.tpe.controller;

import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ShopAuthenticationProvider;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import com.api.auth.service.AuthService;
import com.api.bank.model.entity.Shop;
import com.api.bank.model.entity.Tpe;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.repository.ShopRepository;
import com.api.bank.repository.TpeRepository;
import com.api.bank.service.ShopService;
import com.api.gateway.handler.TpeWebSocketClientTestsHandler;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.service.TransactionRequestService;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeType;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TpeManagerTests {

    private final WebSocketStompClient stompClient;
    private TestRestTemplate testRestTemplate;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TpeRepository tpeRepository;
    private final ShopRepository shopRepository;
    private final ShopService shopService;
    private final TransactionRequestService transactionRequestService;
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final TpeAuthenticationProvider tpeAuthenticationProvider;
    private final ShopAuthenticationProvider shopAuthenticationProvider;

    private String tpeToken;
    private String shopToken;
    private final String tpeAndroidId = "AndroidId";
    private final String shopName = "ShopName";
    private StompSession tpeStompSession;
    private StompSession shopStompSession;
    private final TpeWebSocketClientTestsHandler webSocketClientTestsHandler = new TpeWebSocketClientTestsHandler();


    @Autowired
    public TpeManagerTests(
            WebSocketStompClient stompClient,
            TestRestTemplate testRestTemplate,
            RedisTemplate redisTemplate,
            TransactionRequestService transactionRequestService,
            AuthService authService,
            PasswordEncoder passwordEncoder,
            TpeRepository tpeRepository,
            ShopRepository shopRepository,
            ShopService shopService,
            JWTUtil jwtUtil,
            TpeAuthenticationProvider tpeAuthenticationProvider,
            ShopAuthenticationProvider shopAuthenticationProvider
    ) {
        this.stompClient = stompClient;
        this.testRestTemplate = testRestTemplate;
        this.redisTemplate = redisTemplate;
        this.transactionRequestService = transactionRequestService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.tpeRepository = tpeRepository;
        this.shopRepository = shopRepository;
        this.shopService = shopService;
        this.jwtUtil = jwtUtil;
        this.tpeAuthenticationProvider = tpeAuthenticationProvider;
        this.shopAuthenticationProvider = shopAuthenticationProvider;
    }

    private void initTpe() {
        // Register a fake TPE to the TPE Manager
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

    private void initShop() {
        // Register a fake Shop to the Shop Manager
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        shopOptional.ifPresent(shopRepository::delete);

        String randomPassword = authService.alphaNumericString(12);

        Shop shop = new Shop(shopName, randomPassword);
        shop.setWhitelisted(true);
        shopRepository.save(shop);

        // Get a valid token for the TPE
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(shopName, randomPassword);
        shopAuthenticationProvider.authenticate(authInputToken);
        shopToken = jwtUtil.generateToken(shopName, "Shop Connection");
    }

    private StompSession initStompSession(
            WebSocketHttpHeaders headers, StompHeaders stompHeaders, String token, String endpoint
    )
            throws ExecutionException, InterruptedException, TimeoutException {
        headers.add("Authorization", "Bearer " + token);
        stompHeaders.add("Authorization", "Bearer " + token);

        return stompClient.connect(
                "http://localhost:5080/websocket-manager/secured/tpe/socket",
                headers,
                stompHeaders,
                webSocketClientTestsHandler
        ).get(1, java.util.concurrent.TimeUnit.SECONDS);
    }

    @BeforeAll
    public void beforeAll() {
        // Init TPE
        initTpe();

        // Init Shop
        initShop();
    }

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Connection", "upgrade");
        headers.add("Upgrade", "WebSocket");

        StompHeaders stompHeaders = new StompHeaders();

        tpeStompSession = initStompSession(headers, stompHeaders, tpeToken, "/websocket-manager/secured/tpe/socket");
        shopStompSession = initStompSession(headers, stompHeaders, shopToken, "/websocket-manager/secured/shop/socket");
    }

    @AfterEach
    public void teardown() {
        redisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeAndroidId);
        tpeStompSession.disconnect();
    }

    @AfterAll
    public void afterAll() {
        // Delete the fake TPE from the TPE Manager
        Optional<Tpe> tpeOptional = tpeRepository.findByAndroidId(tpeAndroidId);
        tpeOptional.ifPresent(tpeRepository::delete);

        // Delete the fake Shop from the TPE Manager
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        shopOptional.ifPresent(shopRepository::delete);
    }

    public void sendSynchronizeMessage() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/synchronize");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    public void sendCompleteMessage() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/complete-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("PaymentId", "CARD");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    @Test
    public void testSynchronizeTpeRedis() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE should be in the Redis (synchronized)
        assertEquals(
                "TPE available for transaction.",
                webSocketClientTestsHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SYNCHRONIZED,
                WebSocketStatus.valueOf((String) webSocketClientTestsHandler.getMessage().getType())
        );
    }

    @Test
    public void testAlreadySynchronizedTpeRedis() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE should be in the Redis (synchronized)
        assertEquals(
                "TPE available for transaction.",
                webSocketClientTestsHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SYNCHRONIZED,
                WebSocketStatus.valueOf((String) webSocketClientTestsHandler.getMessage().getType())
        );

        // Ask for synchronization message again
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE is already synchronized
        assertEquals(
                "TPE already synchronised to Redis.",
                webSocketClientTestsHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.ALREADY_SYNCHRONIZED,
                WebSocketStatus.valueOf((String) webSocketClientTestsHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionTpeRedis() throws InterruptedException {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = new TransactionRequest(
                webSocketClientTestsHandler.getSessionId(),
                tpeAndroidId,
                UUID.randomUUID().toString(),
                "ShopName",
                50,
                "PaymentId",
                PaymentMethod.CARD.toString()
        );
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);

        // Remove manually TPE from the Redis
        System.out.println(redisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, tpeAndroidId));
        redisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeAndroidId);

        // Send a synchronization message
        sendCompleteMessage();
        Thread.sleep(2000);

        // TPE should be in the Redis (synchronized)
        assertEquals(
                "TPE available for transaction.",
                webSocketClientTestsHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SYNCHRONIZED,
                WebSocketStatus.valueOf((String) webSocketClientTestsHandler.getMessage().getType())
        );
    }

}
