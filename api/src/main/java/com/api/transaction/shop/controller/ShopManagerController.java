package com.api.transaction.shop.controller;

import com.api.auth.security.JWTUtil;
import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
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
import java.util.UUID;

@Controller
public class ShopManagerController {

    public static final String HASH_KEY_NAME_TPE = "TPE";
    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    public static final String MESSAGE_PREFIX_SHOP = "/queue/shop";
    public static final String MESSAGE_PREFIX_TPE = "/queue/tpe";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ShopRepository shopRepository;

    private Shop getShopByToken(Object token) {
        String identifier = jwtUtil.validateTokenAndRetrieveSubject(token.toString(), "Shop Connection");
        Optional<Shop> shop = shopRepository.findByName(identifier);
        return shop.orElse(null);
    }

    /* Shop */
    @MessageMapping("/shop/pay")
    // @param transactionData must have shopName and amount at this point
    public void pay(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        String destinationShop = String.format("%s/processing/%s", MESSAGE_PREFIX_SHOP, sessionId);
        String destinationShopInvalid = String.format("%s/transaction-invalid/%s", MESSAGE_PREFIX_SHOP, sessionId);
        String destinationTpe = String.format("%s/transaction-opened/", MESSAGE_PREFIX_TPE);
        Gson gson = new Gson();
        // Get Shop from transaction data
        TransactionRequestShop transactionRequestShop = gson.fromJson(transactionData, TransactionRequestShop.class);
        transactionRequestShop.setName(user.getName());
        transactionRequestShop.setSessionId(sessionId);
        Shop shop = getShopByToken(transactionRequestShop.getToken());

        if (shop == null) {
            this.smt.convertAndSendToUser(user.getName(), destinationShopInvalid, new Message("Shop not found.", MessageType.ERROR));
        } else {
            transactionRequestShop.setId(shop.getId().toString());
            // Create transaction request object from transaction request shop
            TransactionRequest transactionRequest = new TransactionRequest(transactionRequestShop);
            try {
                List<Object> tpeManagers = this.customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TPE);
                if (tpeManagers.size() > 0) {
                    // Get a random TPE to process the transaction
                    Object randomMac = this.customRedisTemplate.opsForHash().randomKey(HASH_KEY_NAME_TPE);
                    if (randomMac != null) {
                        Object randomSessionId = this.customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, randomMac);
                        TpeManager tpeManager = new TpeManager(randomMac.toString(), randomSessionId.toString());

                        this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeManager.getId());

                        // Create random identifier for the transaction and register it to REDIS
                        transactionRequest.setId(UUID.randomUUID().toString());
                        transactionRequest.setTpeMac(tpeManager.getId());
                        this.customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());

                        // Send transaction request to TPE and processing request to Shop
                        this.smt.convertAndSendToUser(tpeManager.getId(), destinationTpe + tpeManager.getSessionId(), new Message("Open Transaction.", MessageType.SUCCESS));
                        this.smt.convertAndSendToUser(user.getName(), destinationShop, new Message(gson.toJson(transactionRequest), MessageType.SUCCESS));
                    } else {
                        this.smt.convertAndSendToUser(user.getName(), destinationShopInvalid, new Message("Error while selecting TPE.", MessageType.ERROR));
                    }
                } else {
                    this.smt.convertAndSendToUser(user.getName(), destinationShopInvalid, new Message("No TPE available.", MessageType.ERROR));
                }
            } catch (Exception e) {
                this.smt.convertAndSendToUser(user.getName(), destinationShopInvalid, new Message("Error while processing transaction.", MessageType.ERROR));
            }
        }
    }

}
