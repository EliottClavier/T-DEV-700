package com.api.gateway.shop.service;

import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.service.TransactionRequestService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;

@Service
public class ShopManagerService {
    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;
    @Autowired
    private TpeManagerService tpeManagerService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Used to set the transaction request in redis with Shop infos and random selected TPE infos
     *
     * @param transactionRequest the transaction request containing shop infos and amount of transaction
     * @param tpeUsername the random selected TPE username
     * @return the transaction request on which we set the TPE infos
     */
    public TransactionRequest prepareTransactionRequestFromShop(TransactionRequest transactionRequest, String tpeUsername) {
        Object randomSessionId = tpeManagerService.getTpeRedisSessionIdByUsername(tpeUsername);
        TpeManager tpeManager = new TpeManager(tpeUsername, randomSessionId.toString());
        tpeManagerService.deleteTpeRedisByTpeUsername(tpeUsername);

        // Create random identifier for the transaction and register it to REDIS
        transactionRequest.setTpeUsername(tpeManager.getUsername());
        transactionRequest.setTpeSessionId(tpeManager.getSessionId());
        transactionRequestService.updateTransactionRequestRedis(transactionRequest);
        return transactionRequest;
    }

    /**
     * Method to determine if shop is valid (present in database and whitelisted)
     *
     * @param shopName name of the shop
     * @return true if shop is valid, false otherwise
     */
    public Boolean isShopValid(String shopName) {
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        return shopOptional.isPresent() && shopOptional.get().getWhitelisted();
    }

    /**
     * Used to retrieve a transaction request from redis by Shop session id
     *
     * @param sessionId the session ID of the Shop
     * @return the transaction request, null if no transaction request found
     */
    public TransactionRequest retrieveTransactionRequestByShopSessionId(String sessionId) {
        // For each transaction return value
        for (String key : customRedisTemplate.keys(HASH_KEY_NAME_TRANSACTION + ":*")) {
            TransactionRequest transactionRequest = new Gson().fromJson(customRedisTemplate.opsForValue().get(key), TransactionRequest.class);
            if (transactionRequest.getShopSessionId().equals(sessionId)) {
                return transactionRequest;
            }
        }
        return null;
    }

}
