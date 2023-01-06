package com.api.gateway.service;

import com.api.bank.model.enums.PaymentMethod;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.*;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TpeManagerServiceTests {

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    private final TpeManagerService tpeManagerService;
    private final TransactionRequestService transactionRequestService;

    private final String tpeAndroidId = "tpeAndroidId";
    private final String tpeSessionId = "tpeSessionId";

    @Autowired
    public TpeManagerServiceTests(
            TpeManagerService tpeManagerService,
            TransactionRequestService transactionRequestService
    ) {
        this.tpeManagerService = tpeManagerService;
        this.transactionRequestService = transactionRequestService;
    }

    /**
     * Create transaction request for Redis
     */
    private TransactionRequest putTransactionRequest(String tpeSessionId) {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = new TransactionRequest(
                tpeSessionId,
                "tpeAndroidId",
                "shopSessionId",
                "shopName",
                50,
                "000-000-000-000",
                PaymentMethod.CARD.toString()
        );
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);
        return transactionRequest;
    }

    @AfterEach
    public void teardown() {
        // Empty TPE from Redis
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId)) {
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeAndroidId);
        }
    }

    @Test
    public void testAddTpeRedis() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Add a TPE to the Redis
        Message message = tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Assert message data
        assertEquals(message.getMessage(), "TPE available for transaction.");
        assertEquals(message.getType(), WebSocketStatus.SYNCHRONIZED);
    }

    @Test
    public void testAddTpeRedisAlreadySynchronized() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Try to add the TPE again
        Message message = tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Assert message data
        assertEquals(message.getMessage(), "TPE already synchronised to Redis.");
        assertEquals(message.getType(), WebSocketStatus.ALREADY_SYNCHRONIZED);
    }

    @Test
    public void testIsValidPaymentMethod() {
        assertTrue(tpeManagerService.isValidPaymentMethod(PaymentMethod.CARD.toString()));
        assertFalse(tpeManagerService.isValidPaymentMethod("NOT_CARD"));
    }

    @Test
    public void testRetrievedTransactionRequestByTpeSessionId() {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = putTransactionRequest(tpeSessionId);

        // Retrieve transaction request by TPE session id
        TransactionRequest transactionRequestRetrieved = tpeManagerService.retrieveTransactionRequestByTpeSessionId(tpeSessionId);
        assertNotNull(transactionRequestRetrieved);
        assertEquals(transactionRequest.getId(), transactionRequestRetrieved.getId());
    }

    @Test
    public void testNotRetrievedTransactionRequestByTpeSessionId() {
        // Retrieve null transaction request by TPE session id
        TransactionRequest transactionRequestRetrieved = tpeManagerService.retrieveTransactionRequestByTpeSessionId(tpeSessionId);
        assertNull(transactionRequestRetrieved);
    }

    @Test
    public void testIsTpeRedisAvailable() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(tpeManagerService.isTpeRedisAvailable(tpeAndroidId));
    }

    @Test
    public void testDeleteTpeRedisByTpeUsername() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Delete TPE from Redis
        tpeManagerService.deleteTpeRedisByTpeUsername(tpeAndroidId);

        // Check if the TPE is not in the Redis
        assertFalse(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));
    }

    @Test
    public void testGetAllTpeRedis() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Get all TPE from Redis
        List<Object> tpeManagers = tpeManagerService.getAllTpeRedis();

        // Check if the TPE list is empty
        assertEquals(tpeManagers.size(), 0);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Get all TPE from Redis
        List<Object> tpeManagersAgain = tpeManagerService.getAllTpeRedis();

        // Check if the TPE list is not empty
        assertTrue(tpeManagersAgain.size() > 0);
    }

    @Test
    public void testGetRandomTpeRedisUsername() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Get a random TPE from Redis (should be null)
        Object tpeManagerRetrieved = tpeManagerService.getRandomTpeRedisUsername();

        // Check if the TPE is not null
        assertNull(tpeManagerRetrieved);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Get a random TPE from Redis
        Object tpeManagerRetrievedAgain = tpeManagerService.getRandomTpeRedisUsername();

        // Check if the TPE is not null
        assertNotNull(tpeManagerRetrievedAgain);
    }

    @Test
    public void testGetTpeRedisSessionIdByUsername() {
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);

        // Get TPE session id from Redis
        Object tpeSessionIdRetrieved = tpeManagerService.getTpeRedisSessionIdByUsername(tpeAndroidId);

        // Check if the TPE session id is null
        assertNull(tpeSessionIdRetrieved);

        // Add a TPE to the Redis
        tpeManagerService.addTpeRedis(tpeManager);

        // Check if the TPE is in the Redis
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Get TPE session id from Redis
        Object tpeSessionIdRetrievedAgain = tpeManagerService.getTpeRedisSessionIdByUsername(tpeAndroidId);

        // Check if the TPE session id is not null
        assertNotNull(tpeSessionIdRetrievedAgain);
    }

}
