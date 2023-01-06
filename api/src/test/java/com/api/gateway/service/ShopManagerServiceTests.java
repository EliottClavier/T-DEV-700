package com.api.gateway.service;

import com.api.bank.model.entity.Shop;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.repository.ShopRepository;
import com.api.bank.service.ShopService;
import com.api.gateway.shop.service.ShopManagerService;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopManagerServiceTests {

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    private final ShopManagerService shopManagerService;
    private final TpeManagerService tpeManagerService;
    private final TransactionRequestService transactionRequestService;
    private final ShopRepository shopRepository;

    @Value("${default.shop.username}")
    private String shopName;
    private final String shopSessionId = "shopSessionId";
    private final String tpeAndroidId = "tpeAndroidId";
    private final String tpeSessionId = "tpeSessionId";

    @Autowired
    public ShopManagerServiceTests(
            ShopManagerService shopManagerService,
            TpeManagerService tpeManagerService,
            TransactionRequestService transactionRequestService,
            ShopRepository shopRepository
    ) {
        this.shopManagerService = shopManagerService;
        this.tpeManagerService = tpeManagerService;
        this.transactionRequestService = transactionRequestService;
        this.shopRepository = shopRepository;
    }

    private void setShopWhitelistedStatus(Boolean whitelisted) {
        // Change whitelist status of the shop find by shopName
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        shopOptional.ifPresent(shop -> {
            shop.setWhitelisted(whitelisted);
            shopRepository.save(shop);
        });
    }

    /**
     * Create transaction request for Redis
     */
    private TransactionRequest putTransactionRequest(String shopSessionId) {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = new TransactionRequest(
                "tpeSessionId",
                "tpeAndroidId",
                shopSessionId,
                "shopName",
                50,
                "000-000-000-000",
                PaymentMethod.CARD.toString()
        );
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);
        return transactionRequest;
    }

    @BeforeEach
    public void setUp() {
        // Set shop whitelisted status to true
        setShopWhitelistedStatus(true);
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
    }

    @Test
    public void testPrepareTransactionRequestFromShop() {
        // Synchronize TPE
        TpeManager tpeManager = new TpeManager(tpeAndroidId, tpeSessionId);
        Message message = tpeManagerService.addTpeRedis(tpeManager);

        // Assert TPE is synchronized
        assertEquals(message.getType(), WebSocketStatus.SYNCHRONIZED);
        assertTrue(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));

        // Prepare transaction with Shop information
        TransactionRequest transactionRequest = new TransactionRequest(shopName, shopSessionId, 50);
        TransactionRequest transactionRequestCompleted =
                shopManagerService.prepareTransactionRequestFromShop(transactionRequest, tpeAndroidId);

        // Assert Shop infos have been correctly set
        assertEquals(transactionRequestCompleted.getShopUsername(), transactionRequest.getShopUsername());
        assertEquals(transactionRequestCompleted.getShopSessionId(), transactionRequest.getShopSessionId());
        assertEquals(transactionRequestCompleted.getAmount(), transactionRequest.getAmount());

        // Assert TPE infos in transaction request have been completed
        assertNotNull(transactionRequestCompleted.getTpeSessionId());
        assertNotNull(transactionRequestCompleted.getTpeSessionId());

        // Assert TPE has been removed from Redis
        assertFalse(customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeAndroidId));
    }

    @Test
    public void testIsShopValid() {
        // Assert shop is valid because it is present and whitelisted
        assertTrue(shopManagerService.isShopValid(shopName));
    }

    @Test
    public void testIsNotShopValid() {
        // Set shop whitelisted status to false
        setShopWhitelistedStatus(false);
        // Assert shop is not valid because it is not whitelisted
        assertFalse(shopManagerService.isShopValid(shopName));

        // Assert shop which does not exist is not valid
        assertFalse(shopManagerService.isShopValid(shopName));
    }

    @Test
    public void testRetrievedTransactionRequestByTpeSessionId() {
        // Add a transaction request to the Redis
        TransactionRequest transactionRequest = putTransactionRequest(shopSessionId);

        // Retrieve transaction request by Shop session id
        TransactionRequest transactionRequestRetrieved =
                shopManagerService.retrieveTransactionRequestByShopSessionId(shopSessionId);
        assertNotNull(transactionRequestRetrieved);
        assertEquals(transactionRequest.getId(), transactionRequestRetrieved.getId());
    }

    @Test
    public void testNotRetrievedTransactionRequestByTpeSessionId() {
        // Retrieve null transaction request by Shop session id
        TransactionRequest transactionRequestRetrieved =
                shopManagerService.retrieveTransactionRequestByShopSessionId(tpeSessionId);
        assertNull(transactionRequestRetrieved);
    }

}
