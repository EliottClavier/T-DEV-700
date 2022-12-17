package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.GenericRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService extends GenericService<Account> {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        super(accountRepository);
        this.accountRepository = accountRepository;
    }
    public AccountService() {
        super();
        accountRepository = null;
    }

    public Account getAccountByCardId(String cardId){
        return ((AccountRepository)repository).findAccountByCard_CardId(cardId);
    }
    public Account getAccountByClientId(UUID clientId){
        return ((AccountRepository)repository).findAccountByClient_Id(clientId);
    }
    public Account getAccountByOwnerName(String owner){
        return ((AccountRepository)repository).findAccountByClient_OrganisationName(owner);
    }

    public Account getAccountByClient(Client client){
        return ((AccountRepository)repository).findAccountByClient(client);
    }


}

