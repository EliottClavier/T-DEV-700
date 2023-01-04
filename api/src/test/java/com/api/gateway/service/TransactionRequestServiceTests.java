package com.api.gateway.service;

import com.api.bank.model.enums.PaymentMethod;
import com.api.gateway.transaction.model.Session;
import com.api.gateway.transaction.model.SessionOrigin;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionRequestServiceTests {

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    private final TransactionRequestService transactionRequestService;

    private final String shopSessionId = "shopSessionId";
    private final String shopName = "shopName";
    private final String tpeAndroidId = "tpeAndroidId";
    private final String tpeSessionId = "tpeSessionId";

    @Autowired
    public TransactionRequestServiceTests(
            TransactionRequestService transactionRequestService
    ) {
        this.transactionRequestService = transactionRequestService;
    }

    /**
     * Create transaction request for Redis
     */
    private TransactionRequest putTransactionRequest(String shopSessionId) {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = new TransactionRequest(
                tpeSessionId,
                tpeAndroidId,
                shopSessionId,
                shopName,
                50,
                "000-000-000-000",
                PaymentMethod.CARD.toString()
        );
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);
        return transactionRequest;
    }

    @AfterEach
    public void teardown() {
        // Empty Transaction Requests from Redis
        customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).forEach(
                (key, value) -> {
                    customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + key);
                    customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, key);
                }
        );
    }

    @Test
    public void testIsShopAlreadyInTransaction() {
        Session session = new Session(shopName, shopSessionId);

        // Assert there is no transaction belonging to the shop
        assertFalse(transactionRequestService.isShopAlreadyInTransaction(session));

        // Add a transaction request to the Redis
        putTransactionRequest("fakeShopSessionId");

        // Assert there is no transaction belonging to the shop again
        assertFalse(transactionRequestService.isShopAlreadyInTransaction(session));

        // Add a transaction request to the Redis that matches the shop session ID
        putTransactionRequest(shopSessionId);

        // Assert there is a transaction belonging to the shop
        assertTrue(transactionRequestService.isShopAlreadyInTransaction(session));
    }

    @Test
    public void testDeleteTransactionRequestByShopSessionId() {
        Session session = new Session(shopName, shopSessionId);

        // Add a transaction request to the Redis
        putTransactionRequest(shopSessionId);

        // Assert there is a transaction belonging to the shop
        assertTrue(transactionRequestService.isShopAlreadyInTransaction(new Session(shopName, shopSessionId)));

        // Delete the transaction request by Shop session ID
        Session sessionResponse = transactionRequestService.deleteTransactionRequestBySessionId(session);

        // Assert session response has TPE information
        assertEquals(sessionResponse.getUsername(), tpeAndroidId);
        assertEquals(sessionResponse.getSessionId(), tpeSessionId);
        assertEquals(sessionResponse.getOrigin(), SessionOrigin.TPE);
    }

    @Test
    public void testDeleteTransactionRequestByTpeSessionId() {
        Session session = new Session(tpeAndroidId, tpeSessionId);

        // Add a transaction request to the Redis
        putTransactionRequest(shopSessionId);

        // Assert there is a transaction belonging to the shop
        assertTrue(transactionRequestService.isShopAlreadyInTransaction(new Session(shopName, shopSessionId)));

        // Delete the transaction request by TPE session ID
        Session sessionResponse = transactionRequestService.deleteTransactionRequestBySessionId(session);

        // Assert session response has TPE information
        assertEquals(sessionResponse.getUsername(), shopName);
        assertEquals(sessionResponse.getSessionId(), shopSessionId);
        assertEquals(sessionResponse.getOrigin(), SessionOrigin.SHOP);
    }

    @Test
    public void testIsTransactionRedisKey() {
        assertTrue(transactionRequestService.isTransactionRedisKey(
                HASH_KEY_NAME_TRANSACTION + ":" + UUID.randomUUID())
        );
    }

    @Test
    public void testIsNotTransactionRedisKey() {
        assertFalse(transactionRequestService.isTransactionRedisKey(
                "NOT_REAL_TRANSACTION" + ":" + UUID.randomUUID())
        );
    }

    @Test
    public void testDeleteTransactionRequestRedisHashByUUID() {
        // Create a transaction request
        TransactionRequest transactionRequest = putTransactionRequest(shopSessionId);

        // Assert that the transaction is present in Redis
        assertNotNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId()));
        assertTrue(customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size() > 0);

        // Delete the transaction request by UUID
        transactionRequestService.deleteTransactionRequestRedisHashByUUID(transactionRequest.getId());

        // Assert that the transaction is removed from the Redis
        assertNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":"));
        assertEquals(customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size(), 0);
    }

    @Test
    public void testGetTransactionRequestRedisHashByUUID() {
        // Create a transaction request
        TransactionRequest transactionRequest = putTransactionRequest(shopSessionId);

        // Assert that the transaction is removed from the Redis
        assertNotNull(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId()));
        assertTrue(customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).size() > 0);

        // Delete the transaction request by UUID
        TransactionRequest transactionRequestRetrieved =
                transactionRequestService.getTransactionRequestRedisHashByUUID(transactionRequest.getId());

        // Assert that the transaction is retrieved from the Redis is the same as the one created
        assertEquals(transactionRequestRetrieved.getId(), transactionRequest.getId());
    }

    @Test
    public void testUpdateTransactionRequestRedis() {
        String paymentId = "000-000-000-000";
        PaymentMethod paymentMethod = PaymentMethod.CARD;

        // Create a transaction request like it was coming from Shop
        TransactionRequest transactionRequest = new TransactionRequest(
                tpeSessionId,
                tpeAndroidId,
                shopSessionId,
                shopName,
                50,
                "999-999-999-999",
                PaymentMethod.TRANSFER.toString()
        );

        // Save it to Redis
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);

        // Retrieve it
        TransactionRequest transactionRequestRetrieved =
                transactionRequestService.getTransactionRequestRedisHashByUUID(transactionRequest.getId());

        // Assert that payment ID and payment method are null
        assertNotEquals(transactionRequestRetrieved.getPaymentId(), paymentId);
        assertNotEquals(transactionRequestRetrieved.getPaymentMethod(), paymentMethod);

        // Set the payment ID and payment method in transaction request instance
        transactionRequest.setPaymentId(paymentId);
        transactionRequest.setPaymentMethod(paymentMethod);

        // Update the transaction request in Redis
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);

        // Retrieve it again from Redis
        TransactionRequest transactionRequestRetrievedAgain =
                transactionRequestService.getTransactionRequestRedisHashByUUID(transactionRequest.getId());

        // Assert that payment ID and payment method have been updated in Redis and are not null
        assertEquals(transactionRequestRetrievedAgain.getPaymentId(), paymentId);
        assertEquals(transactionRequestRetrievedAgain.getPaymentMethod(), paymentMethod);
    }


}
