package com.api.transaction.shop.controller;

import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import com.api.transaction.model.Message;
import com.api.transaction.model.MessageType;
import com.api.transaction.model.Transaction;
import com.api.transaction.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ShopManagerController {

    public static final String HASH_KEY_NAME = "TPE";
    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ShopRepository shopRepository;

    /* Shop */
    @MessageMapping("/shop/pay")
    // @param transactionData must have shopName and amount at this point
    public void pay(@Payload String transactionData) {
        String responseDestinationShop = "/private/shop/processing";
        String responseDestinationTpe = "/private/tpe/open-transaction";
        Gson gson = new Gson();
        Transaction transaction = gson.fromJson(transactionData, Transaction.class);
        try {
            Shop shop = this.shopRepository.findByName(transaction.getShopName()).get();
            List<Object> tpeManagers = this.customRedisTemplate.opsForHash().values(HASH_KEY_NAME);
            if (tpeManagers.size() > 0) {
                Object randomMac = this.customRedisTemplate.opsForHash().randomKey(HASH_KEY_NAME);
                Object randomIp = this.customRedisTemplate.opsForHash().get(HASH_KEY_NAME, randomMac);
                TpeManager tpeManager = new TpeManager(randomMac.toString(), randomIp.toString());
                this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME, tpeManager.getId());
                transaction.setTpeMac(tpeManager.getId());
                this.simpMessagingTemplate.convertAndSendToUser(tpeManager.getId(), responseDestinationTpe, new Message("Open Transaction", MessageType.SUCCESS));
                this.simpMessagingTemplate.convertAndSendToUser(shop.getName(), responseDestinationShop, new Message(gson.toJson(transaction), MessageType.SUCCESS));
            } else {
                this.simpMessagingTemplate.convertAndSendToUser(shop.getName(), responseDestinationShop, new Message("No TPE available", MessageType.ERROR));
            }
        } catch (Exception e) {
            this.simpMessagingTemplate.convertAndSend(
                    responseDestinationShop,
                    new Message("Error while processing transaction", MessageType.ERROR)
            );
        }
    }

}
