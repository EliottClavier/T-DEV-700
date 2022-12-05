package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.GenericRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends GenericService<Account, GenericRepository<Account>> {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository repository) {
        super(repository);
        accountRepository = repository;
    }


}

