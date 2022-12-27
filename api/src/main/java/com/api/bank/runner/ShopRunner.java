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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ShopRunner implements ApplicationRunner {

    @Autowired
    private ShopService shopService;

    @Autowired
    private AccountService accountService;

    @Value("${default.shop.username}")
    private String username;

    @Value("${default.shop.password}")
    private String password;

    public void run(ApplicationArguments args) {
        Shop shop = new Shop(username, password);
        Shop shopRegisterResponse = shopService.registerShop(shop);

        if (shopRegisterResponse != null) {
            Client client = new Client(shop.getId(), shop.getName(), SocialReasonStatus.COMPANY);
            Account account = new Account(0, client);
            accountService.add(account);
        }
    }
}
