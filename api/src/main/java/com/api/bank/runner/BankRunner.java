package com.api.bank.runner;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.api.bank.model.BankConstants.BANK_ID;
import static com.api.bank.model.BankConstants.BANK_NAME;

@Component
public class BankRunner implements ApplicationRunner {

    private final AccountService accountService;

    @Autowired
    public BankRunner(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Account accountSearch = accountService.getAccountByOwnerName(BANK_NAME);
        if (accountSearch == null) {
            Client client = new Client(UUID.randomUUID(), BANK_NAME, SocialReasonStatus.BANK);
            Account account = new Account(UUID.fromString(BANK_ID), 100000000, client);

            accountService.add(account);
        }
    }

}
