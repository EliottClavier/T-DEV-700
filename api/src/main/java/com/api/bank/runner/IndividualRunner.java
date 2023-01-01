package com.api.bank.runner;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class is used to create a default Individual account
 */
@Component
public class IndividualRunner implements ApplicationRunner {

    private final AccountService accountService;

    @Autowired
    public IndividualRunner(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) {
        UUID id = UUID.fromString("e45f2bc3-1ef0-4ed9-94b3-146483c66bea");
        Account accountSearch = accountService.getAccountByClientId(id);
        if (accountSearch == null) {
            Client client = new Client(id, "Ivain", "Lamarche", SocialReasonStatus.INDIVIDUAL);
            Card card = new Card("001-043-191-039");
            Account account = new Account(5000, client, card);

            accountService.add(account);
        }
    }
}
