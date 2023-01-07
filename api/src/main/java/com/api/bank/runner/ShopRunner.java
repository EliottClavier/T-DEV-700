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

    // Create a default shop account following environment variables
    // This is basically the shop account that will be used by the Shop application
    public void run(ApplicationArguments args) {

        try {
            var shop = shopService.getShopByName(username);

            if (shop == null) {
                shop = new Shop(UUID.randomUUID().toString(), username, password);
                shopService.registerShop(shop);
            }

            if (!shop.getWhitelisted()) {
                shopService.updateShopStatus(shop.getId().toString(), true);
            }

            Account accountSearch = accountService.getAccountByOwnerName(username);

            if (accountSearch == null) {
                Client client = new Client(shop.getId(), shop.getName(), SocialReasonStatus.COMPANY);
                Account account = new Account(3000, client);
                accountService.add(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

