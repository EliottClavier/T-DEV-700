package com.api.bank.runner;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.model.entity.Shop;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import com.api.bank.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ShopRunner implements ApplicationRunner {

    private final ShopService shopService;
    private final AccountService accountService;


    @Autowired
    public ShopRunner(AccountService accountService, ShopService shopService) {
        this.accountService = accountService;
        this.shopService = shopService;
    }

    @Value("${default.shop.username}")
    private String username;

    @Value("${default.shop.password}")
    private String password;

    @Value("${default.shop.client.id}")
    private String shopId;

    // Create a default shop account following environment variables
    // This is basically the shop account that will be used by the Shop application
    public void run(ApplicationArguments args) {

        Account accountSearch = accountService.getAccountByClientId(UUID.fromString(shopId));
        if (accountSearch == null) {

            Shop shop = new Shop(shopId, username, password);
            Shop shopRegisterResponse = shopService.registerShop(shop);
            shopService.updateShopStatus(shopRegisterResponse.getId().toString(), true);
            Client client = new Client(shop.getId(), shop.getName(), SocialReasonStatus.COMPANY);
            Account account = new Account(1500, client);
            accountService.add(account);
        }
    }
}
