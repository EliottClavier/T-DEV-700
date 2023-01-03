package com.api.gateway.controller;

import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ShopAuthenticationProvider;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import com.api.auth.service.AuthService;
import com.api.bank.model.BankConstants;
import com.api.bank.model.entity.*;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.repository.*;
import com.api.gateway.handler.ShopWebSocketClientTestsHandler;
import com.api.gateway.handler.TpeWebSocketClientTestsHandler;
import com.api.gateway.handler.WebSocketClientTestsHandler;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.model.TransactionRequestTpe;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.service.TransactionRequestService;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketTests {

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    private final WebSocketStompClient stompClient;
    private TestRestTemplate testRestTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TpeRepository tpeRepository;
    private final ShopRepository shopRepository;
    private AccountRepository accountRepository;
    private final TransactionRequestService transactionRequestService;
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final TpeAuthenticationProvider tpeAuthenticationProvider;
    private final ShopAuthenticationProvider shopAuthenticationProvider;

    private String tpeToken;
    private String shopToken;
    private final String tpeAndroidId = "AndroidId";

    @Value("${default.shop.username}")
    private String shopName;

    @Value("${default.shop.password}")
    private String shopPassword;

    private final String paymentId = "000-000-000-000";
    private final String clientName = "ClientName";
    private StompSession tpeStompSession;
    private StompSession shopStompSession;
    private final TpeWebSocketClientTestsHandler tpeHandler = new TpeWebSocketClientTestsHandler();
    private final ShopWebSocketClientTestsHandler shopHandler = new ShopWebSocketClientTestsHandler();


    @Autowired
    public WebSocketTests(
            WebSocketStompClient stompClient,
            TestRestTemplate testRestTemplate,
            TransactionRequestService transactionRequestService,
            AuthService authService,
            JWTUtil jwtUtil,
            TpeAuthenticationProvider tpeAuthenticationProvider,
            ShopAuthenticationProvider shopAuthenticationProvider,
            PasswordEncoder passwordEncoder,
            TpeRepository tpeRepository,
            ShopRepository shopRepository,
            AccountRepository accountRepository
    ) {
        this.stompClient = stompClient;
        this.testRestTemplate = testRestTemplate;
        this.transactionRequestService = transactionRequestService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tpeAuthenticationProvider = tpeAuthenticationProvider;
        this.shopAuthenticationProvider = shopAuthenticationProvider;
        this.tpeRepository = tpeRepository;
        this.shopRepository = shopRepository;
        this.accountRepository = accountRepository;
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
        // Change whitelist status of the shop find by shopName
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        shopOptional.ifPresent(shop -> {
            shop.setWhitelisted(true);
            shopRepository.save(shop);
        });

        // Get a valid token for the Shop
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(shopName, shopPassword);
        shopAuthenticationProvider.authenticate(authInputToken);
        shopToken = jwtUtil.generateToken(shopName, "Shop Connection");
    }

    private void initClient() {
        if (accountRepository.findAccountByCard_CardId(paymentId) == null) {
            Client client = new Client(UUID.randomUUID(), clientName, clientName, SocialReasonStatus.INDIVIDUAL);
            // Create a date that match in two years
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 2);
            Date expirationDate = calendar.getTime();
            Card card = new Card(paymentId, expirationDate);
            Account account = new Account(10000, client, card);

            accountRepository.save(account);
        }
    }

    private StompSession initStompSession(String token, String type, WebSocketClientTestsHandler handler)
            throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Connection", "upgrade");
        headers.add("Upgrade", "WebSocket");
        headers.add("Authorization", "Bearer " + token);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + token);

        return stompClient.connect(
                "http://localhost:5080/websocket-manager/secured/" + type + "/socket",
                headers,
                stompHeaders,
                handler
        ).get(1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void setClientSold(int sold, String clientName) {
        try {
            accountRepository.findAccountByClient_Lastname(clientName).setSold(sold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOrganisationSold(int sold, String organisationName) {
        try {
            accountRepository.findAccountByClient_OrganisationName(organisationName).setSold(sold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putTransactionRequest(String paymentId, String paymentMethod) {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = new TransactionRequest(
                tpeHandler.getSessionId(),
                tpeAndroidId,
                shopHandler.getSessionId(),
                shopName,
                50,
                paymentId,
                paymentMethod
        );
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);
    }

    @BeforeAll
    public void beforeAll() {
        // Init TPE
        initTpe();

        // Init Shop
        initShop();

        // Init client
        initClient();
    }

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        // Create sessions
        tpeStompSession = initStompSession(tpeToken, "tpe", tpeHandler);
        shopStompSession = initStompSession(shopToken, "shop", shopHandler);

        // Set sold
        setClientSold(10000, clientName);
        setOrganisationSold(10000, shopName);
        setOrganisationSold(10000, BankConstants.BANK_NAME);
    }

    @AfterEach
    public void teardown() {
        // Empty TPE from Redis
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId)) {
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeAndroidId);
        }

        // Empty Transaction Requests from Redis
        customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).forEach(
                (key, value) -> {
                    customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + key);
                    customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, key);
                }
        );

        // Clear handlers
        tpeHandler.clear();
        shopHandler.clear();
    }

    @AfterAll
    public void afterAll() {
        // Delete the fake TPE from the TPE Manager
        Optional<Tpe> tpeOptional = tpeRepository.findByAndroidId(tpeAndroidId);
        tpeOptional.ifPresent(tpeRepository::delete);
    }

    public void sendSynchronizeMessage() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/synchronize");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    public void sendCompleteMessage(String paymentId, String paymentMethod) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/complete-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        TransactionRequestTpe message = new TransactionRequestTpe(paymentId, paymentMethod);
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    public void sendCancelMessageFromTpe() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/cancel-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    @Test
    public void testSynchronizeTpe() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE should be in the Redis (synchronized)
        assertEquals(
                "TPE available for transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SYNCHRONIZED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

    @Test
    public void testAlreadySynchronizedTpe() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE should be in the Redis (synchronized)
        assertEquals(
                "TPE available for transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SYNCHRONIZED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Ask for synchronization message again
        sendSynchronizeMessage();
        Thread.sleep(2000);

        // TPE is already synchronized
        assertEquals(
                "TPE already synchronised to Redis.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.ALREADY_SYNCHRONIZED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionCardTpe() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());

        // Send a synchronization message
        sendCompleteMessage(paymentId, PaymentMethod.CARD.toString());
        Thread.sleep(2000);

        // TPE get the transaction result
        assertEquals(
                "Payment has been validated",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                TransactionStatus.SUCCESS,
                TransactionStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the transaction result
        assertEquals(
                "Payment has been validated",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                TransactionStatus.SUCCESS,
                TransactionStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionCardTpeWithFakePayment() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest("999-999-999-999", PaymentMethod.CARD.toString());

        // Send a synchronization message
        sendCompleteMessage("999-999-999-999", PaymentMethod.CARD.toString());
        Thread.sleep(2000);

        // TPE get the transaction result
        assertEquals(
                "Account not found",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                TransactionStatus.ACCOUNT_ERROR,
                TransactionStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the transaction result
        assertEquals(
                "Error while processing transaction.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                TransactionStatus.FAILED,
                TransactionStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionTpeWithInvalidPaymentMethod() throws InterruptedException {
        // Add a transaction request to the Redis (PaymentMethod will be override by the complete message)
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());

        // Send a synchronization message
        sendCompleteMessage(paymentId, "FAKE");
        Thread.sleep(2000);

        // TPE get the transaction result
        assertEquals(
                "Invalid payment method.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.INVALID_PAYMENT_METHOD,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the transaction result
        assertEquals(
                "Error while processing transaction.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_ERROR,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionTpeNotInvolved() throws InterruptedException {
        // Send a synchronization message
        sendCompleteMessage(paymentId, PaymentMethod.CARD.toString());
        Thread.sleep(2000);

        // TPE get the message
        assertEquals(
                "TPE not involved in any transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.NOT_INVOLVED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

    @Test
    public void testCompleteTransactionTpeStillAvailable() throws InterruptedException {
        // Synchronise the TPE to mark it available for transaction
        sendSynchronizeMessage();

        // Send a synchronization message
        sendCompleteMessage(paymentId, PaymentMethod.CARD.toString());
        Thread.sleep(2000);

        // TPE get the message
        assertEquals(
                "TPE still marked available while trying to complete a transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.STILL_AVAILABLE,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

    @Test
    public void testCancelTransactionTpe() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());

        // Send a cancel message
        sendCancelMessageFromTpe();
        Thread.sleep(2000);

        // TPE get the message
        assertEquals(
                "Transaction cancelled.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_CANCELLED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the message
        assertEquals(
                "Transaction cancelled.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_CANCELLED,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testCancelTransactionTpeNotInvolved() throws InterruptedException {
        // Send a cancel message
        sendCancelMessageFromTpe();
        Thread.sleep(2000);

        // TPE get the message
        assertEquals(
                "TPE not involved in any transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.NOT_INVOLVED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

    @Test
    public void testCancelTransactionTpeStillAvailable() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        Thread.sleep(2000);

        System.out.println(tpeHandler.getMessage().getMessage());

        // Send a cancel message
        sendCancelMessageFromTpe();
        Thread.sleep(2000);

        // TPE get the message
        assertEquals(
                "TPE still marked available while trying to cancel a transaction.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.STILL_AVAILABLE,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );
    }

}
