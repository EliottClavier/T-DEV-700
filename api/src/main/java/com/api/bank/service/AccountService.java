package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService extends GenericService<Account, AccountRepository> {
    @Autowired
    public AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        super(accountRepository);
        this.accountRepository = accountRepository;
    }
    public AccountService() {
        super();
    }

    public Account getAccountByCardId(String cardId){
        return accountRepository.findAccountByCard_CardId(cardId);
    }
    public Account getAccountByClientId(UUID clientId){
        return accountRepository.findAccountByClient_Id(clientId);
    }
    public Account getAccountByOwnerName(String owner){
        return accountRepository.findAccountByClient_OrganisationName(owner);
    }

    public Account getAccountByClient(Client client){
        return accountRepository.findAccountByClient(client);
    }


}

