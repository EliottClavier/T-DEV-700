package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends BaseService<Account, AccountRepository > {


    private AccountRepository accountRepository;
    @Autowired
    private ObjectMapper objectMapper;


}

