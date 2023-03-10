package com.api.bank.runner;


import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${default.individual.firstname}")
    private String firstname;

    @Value("${default.individual.lastname}")
    private String lastname;

    @Value("${default.individual.card.id}")
    private String cardId;

    @Autowired
    public IndividualRunner(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) {

        Account accountSearch = accountService.getAccountByCardId(cardId);
        if (accountSearch == null) {
            Client client = new Client(UUID.randomUUID(), firstname, lastname, SocialReasonStatus.INDIVIDUAL);
            Card card = new Card(cardId);
            Account account = new Account(10000, client, card);

            accountService.add(account);
        }
    }
}