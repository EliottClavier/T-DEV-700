package com.api.transaction.shop.controller;

import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import com.api.transaction.data.DestinationGenerator;
import com.api.transaction.model.Message;
import com.api.transaction.model.MessageType;
import com.api.transaction.model.TransactionRequest;
import com.api.transaction.model.TransactionRequestShop;
import com.api.transaction.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ShopManagerController {

    public static final String HASH_KEY_NAME_TPE = "TPE";
    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private ShopRepository shopRepository;

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    /* Shop */
    @MessageMapping("/shop/pay")
    public void pay(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        // Get Shop from transaction data
        Gson gson = new Gson();
        TransactionRequestShop transactionRequestShop = gson.fromJson(transactionData, TransactionRequestShop.class);
        Optional<Shop> shop = this.shopRepository.findByName(user.getName());

        // Cancel transaction if shop does not exist
        if (shop.isEmpty() || !shop.get().getWhitelisted()) {
            this.smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                    new Message("Shop not found.", MessageType.NOT_FOUND)
            );
        } else {
            // Create transaction request object from transaction request shop
            TransactionRequest transactionRequest = new TransactionRequest(user.getName(), sessionId, transactionRequestShop.getAmount());
            try {
                List<Object> tpeManagers = this.customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TPE);
                if (tpeManagers.size() > 0) {
                    // Get a random TPE to process the transaction
                    Object randomId = this.customRedisTemplate.opsForHash().randomKey(HASH_KEY_NAME_TPE);
                    if (randomId != null) {
                        // Select a random TPE and delete it from TPE list
                        Object randomSessionId = this.customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, randomId);
                        TpeManager tpeManager = new TpeManager(randomId.toString(), randomSessionId.toString());
                        this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeManager.getId());

                        // Create random identifier for the transaction and register it to REDIS
                        transactionRequest.setTpeUsername(tpeManager.getId());
                        transactionRequest.setTpeSessionId(tpeManager.getSessionId());
                        this.customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());

                        // Send transaction opened message to TPE
                        this.smt.convertAndSendToUser(
                                transactionRequest.getTpeUsername(),
                                destinationGenerator.getTpeTransactionStatusDest(transactionRequest.getTpeSessionId()),
                                new Message("Open Transaction.", MessageType.TRANSACTION_OPENED)
                        );

                        // Send processing message to Shop
                        this.smt.convertAndSendToUser(
                                user.getName(),
                                destinationGenerator.getShopTransactionStatusDest(sessionId),
                                new Message(gson.toJson(transactionRequest), MessageType.TRANSACTION_OPENED)
                        );
                    } else {
                        this.smt.convertAndSendToUser(
                                user.getName(),
                                destinationGenerator.getShopTransactionStatusDest(sessionId),
                                new Message("Error while selecting TPE.", MessageType.NO_TPE_SELECTED)
                        );
                    }
                } else {
                    this.smt.convertAndSendToUser(
                            user.getName(),
                            destinationGenerator.getShopTransactionStatusDest(sessionId),
                            new Message("No TPE available.", MessageType.NO_TPE_FOUND)
                    );
                }
            } catch (Exception e) {
                this.smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getShopTransactionStatusDest(sessionId),
                        new Message("Error while processing transaction.", MessageType.TRANSACTION_ERROR)
                );
            }
        }
    }

}
