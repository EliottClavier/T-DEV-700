package com.api.gateway.shop.service;

import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.MessageType;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ShopManagerService {
    @Autowired
    private TpeManagerService tpeManagerService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    @Autowired
    private ShopRepository shopRepository;

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

    public Boolean isShopValid(String shopName) {
        Optional<Shop> shopOptional = shopRepository.findByName(shopName);
        return shopOptional.isPresent() && shopOptional.get().getWhitelisted();
    }

}
