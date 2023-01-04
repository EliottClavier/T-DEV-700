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
import com.api.gateway.runner.WebSocketRunner;
import com.api.gateway.transaction.model.*;
import com.api.gateway.transaction.service.TransactionRequestService;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeType;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketTests {

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private WebSocketRunner webSocketRunner;

    private final WebSocketStompClient stompClient;
    private final PasswordEncoder passwordEncoder;
    private final TpeRepository tpeRepository;
    private final ShopRepository shopRepository;
    private final AccountRepository accountRepository;
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

    @Value("${server.port}")
    private String serverPort;

    private final String paymentId = "000-000-000-000";
    private final String clientName = "ClientName";
    private StompSession tpeStompSession;
    private StompSession shopStompSession;
    private final TpeWebSocketClientTestsHandler tpeHandler = new TpeWebSocketClientTestsHandler();
    private final ShopWebSocketClientTestsHandler shopHandler = new ShopWebSocketClientTestsHandler();

    private Long timeToWaitWebSocket = 3L;


    @Autowired
    public WebSocketTests(
            WebSocketStompClient stompClient,
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

    /**
     * Create a TPE and a Shop
     */
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

    /**
     * Set client with account and card
     */
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

    /**
     * Init stomp session (used for TPE and SHOP)
     */
    private StompSession initStompSession(String token, String type, WebSocketClientTestsHandler handler)
            throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Connection", "upgrade");
        headers.add("Upgrade", "WebSocket");
        headers.add("Authorization", "Bearer " + token);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + token);

        return stompClient.connect(
                "http://localhost:" + serverPort + "/websocket-manager/secured/" + type + "/socket",
                headers,
                stompHeaders,
                handler
        ).get(1, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * Set client sold
     */
    private void setClientSold(int sold, String clientName) {
        try {
            accountRepository.findAccountByClient_Lastname(clientName).setSold(sold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set bank and shop sold
     */
    private void setOrganisationSold(int sold, String organisationName) {
        try {
            accountRepository.findAccountByClient_OrganisationName(organisationName).setSold(sold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create transaction request for Redis
     */
    private TransactionRequest putTransactionRequest(String paymentId, String paymentMethod) {
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
        return transactionRequest;
    }

    /**
     * Send sync message from TPE
     */
    public void sendSynchronizeMessage() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/synchronize");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    /**
     * Send complete transaction message from TPE
     */
    public void sendCompleteMessage(String paymentId, String paymentMethod) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/complete-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        TransactionRequestTpe message = new TransactionRequestTpe(paymentId, paymentMethod);
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    /**
     * Send cancel transaction message from TPE
     */
    public void sendCancelMessageFromTpe() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/tpe/cancel-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        tpeStompSession.send(stompHeaders, jsonMessage);
    }

    /**
     * Send pay message from Shop
     */
    public void sendPayMessage(float amount) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/shop/pay");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        TransactionRequestShop transactionRequestShop = new TransactionRequestShop(amount);
        String jsonMessage = new Gson().toJson(transactionRequestShop);
        shopStompSession.send(stompHeaders, jsonMessage);
    }

    /**
     * Send cancel transaction message from Shop
     */
    public void sendCancelMessageFromShop() {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/websocket-manager/shop/cancel-transaction");
        stompHeaders.setContentType(MimeType.valueOf("application/json"));

        Message message = new Message("", "");
        String jsonMessage = new Gson().toJson(message);
        shopStompSession.send(stompHeaders, jsonMessage);
    }

    /**
     * Wait
     */
    private void waitWebSocket() throws InterruptedException {
        Thread.sleep(timeToWaitWebSocket * 1000);
    }

    /**
     * Before All, Before Each, After All, After Each
     */
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

        // Clear sessions
        if (tpeStompSession.isConnected()) {
            tpeStompSession.disconnect();
        }
        if (shopStompSession.isConnected()) {
            shopStompSession.disconnect();
        }
    }

    @AfterAll
    public void afterAll() {
        // Delete the fake TPE from the TPE Manager
        Optional<Tpe> tpeOptional = tpeRepository.findByAndroidId(tpeAndroidId);
        tpeOptional.ifPresent(tpeRepository::delete);
    }

    /**
     *
     * FROM TPE TESTS: TpeManagerController tests (Synchronize, Confirm, Cancel, Disconnect)
     *
     */
    @Test
    public void testSynchronizeTpe() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

        // Send a synchronization message
        sendCompleteMessage(paymentId, PaymentMethod.CARD.toString());
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

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
        waitWebSocket();

        // Send a cancel message
        sendCancelMessageFromTpe();
        waitWebSocket();

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

    /**
     *
     * Disconnect test linked to TPE and located in WebSocketListeners controller
     *
     */
    @Test
    public void testDisconnectTpe() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        waitWebSocket();

        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());
        waitWebSocket();

        // Kill TPE Session
        tpeStompSession.disconnect();
        waitWebSocket();

        // Shop get the message
        assertEquals(
                "Transaction cancelled after losing connection with TPE.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.LOST_CONNECTION,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );

        // Assert that the transaction is removed from the Redis
        assertNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":"));
        assertEquals(customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size(), 0);

        // Assert that the TPE is removed from the Redis
        assertFalse(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));
    }

    /**
     *
     * FROM SHOP TESTS: ShopManagerController tests (Pay, Cancel, Disconnect)
     *
     */
    @Test
    public void testPayShop() throws InterruptedException {
        // Send a synchronization message
        sendSynchronizeMessage();
        waitWebSocket();

        // Send a cancel message
        sendPayMessage(50);
        waitWebSocket();

        // TPE get the message
        assertEquals(
                "Transaction opened.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_OPENED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the message
        assertEquals(
                "Transaction opened.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_OPENED,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testPayShopWithoutTpeAvailable() throws InterruptedException {
        // Send a cancel message
        sendPayMessage(50);
        waitWebSocket();

        // Shop get the message
        assertEquals(
                "No TPE available.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.NO_TPE_FOUND,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testPayShopWhileAlreadyInTransaction() throws InterruptedException {
        // Sync the TPE
        sendSynchronizeMessage();
        waitWebSocket();

        // Send a cancel message
        sendPayMessage(50);
        waitWebSocket();

        // TPE get the message
        assertEquals(
                "Transaction opened.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_OPENED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the message
        assertEquals(
                "Transaction opened.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_OPENED,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );

        // Send a cancel message
        sendPayMessage(50);
        waitWebSocket();

        // Shop get the message
        assertEquals(
                "Shop already in a transaction.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.ALREADY_IN_TRANSACTION,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    @Test
    public void testCancelTransactionShop() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());

        // Send a cancel message
        sendCancelMessageFromShop();
        waitWebSocket();

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
    public void testCancelTransactionShopNotInvolved() throws InterruptedException {
        // Send a cancel message
        sendCancelMessageFromShop();
        waitWebSocket();

        // Shop get the message
        assertEquals(
                "Shop not involved in any transaction.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.NOT_INVOLVED,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );
    }

    /**
     *
     * Disconnect test linked to Shop and located in WebSocketListeners controller
     *
     */
    @Test
    public void testDisconnectShop() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());
        waitWebSocket();

        // Kill Shop Session
        shopStompSession.disconnect();
        waitWebSocket();

        // TPE get the message
        assertEquals(
                "Transaction cancelled after losing connection with Shop.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.LOST_CONNECTION,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Assert that the transaction is removed from the Redis
        assertNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":"));
        assertEquals(0, customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size());
    }

    /**
     *
     * EXPIRATION TESTS: Test for RedisExpirationListener controller
     *
     */
    @Test
    public void testTransactionExpiration() throws InterruptedException {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = putTransactionRequest(paymentId, PaymentMethod.CARD.toString());
        waitWebSocket();

        // Change expiration time artificially (normally it is equal to HASH_KEY_TTL)
        customRedisTemplate.expire(
                HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId(), 1, TimeUnit.MILLISECONDS
        );
        waitWebSocket();

        // TPE get the message (message content is not entirely tested because it's not totally depending on expiration
        // feature but also on synchronization feature)
        assertTrue(
                tpeHandler.getMessage().getMessage().toString().startsWith("Transaction timed out.")
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_TIMED_OUT,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the message
        assertEquals(
                "Transaction timed out.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.TRANSACTION_TIMED_OUT,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );

        // Assert that the transaction is removed from the Redis
        assertNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":"));
        assertEquals(0, customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size());
    }

    /**
     *
     * SERVER RESTART TESTS: Test for WebSocketRunner
     *
     */
    @Test
    public void testServerRestartsDuringTransaction() throws InterruptedException {
        // Add a transaction request to the Redis
        putTransactionRequest(paymentId, PaymentMethod.CARD.toString());
        waitWebSocket();

        // Restart the server
        webSocketRunner.run(new ApplicationArguments() {
            @Override
            public String[] getSourceArgs() {
                return new String[0];
            }

            @Override
            public Set<String> getOptionNames() {
                return null;
            }

            @Override
            public boolean containsOption(String name) {
                return false;
            }

            @Override
            public List<String> getOptionValues(String name) {
                return null;
            }

            @Override
            public List<String> getNonOptionArgs() {
                return null;
            }
        });
        waitWebSocket();

        // Tpe get the message
        assertEquals(
                "Server has restarted.",
                tpeHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SERVER_RESTARTED,
                WebSocketStatus.valueOf((String) tpeHandler.getMessage().getType())
        );

        // Shop get the message
        assertEquals(
                "Server has restarted.",
                shopHandler.getMessage().getMessage()
        );
        assertEquals(
                WebSocketStatus.SERVER_RESTARTED,
                WebSocketStatus.valueOf((String) shopHandler.getMessage().getType())
        );

        // In consequence of the SERVER_RESTARTED messages, client must disconnect
        tpeStompSession.disconnect();
        shopStompSession.disconnect();
        waitWebSocket();

        // It means transactions are killed
        // Assert that the transaction is removed from the Redis
        assertNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":"));
        assertEquals(0, customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size());
    }
}
