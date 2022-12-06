package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends GenericService<Account, AccountRepository > {

    @Autowired
    public AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        super(accountRepository);
    }

    public Account getAccountByCardId(String cardId){
        return accountRepository.findAccountByCard_CardId(cardId);
    }

    public Account getAccountByClient(Client client){
        return accountRepository.findAccountByClient(client);
    }
}

