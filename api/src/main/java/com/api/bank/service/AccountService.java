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

//    public ResponseEntity addAccount(JsonNode accountData) {
//        Account account  = objectMapper.convertValue(accountData, Account.class);
//        accountRepository.saveAndFlush(account);
//        return ResponseEntity.ok(accountData);
//    }
//    public ResponseEntity<JsonNode> getAllAccount() {
//        Account account  = accountRepository.
//
//        return ResponseEntity.ok(accountData);
//    }
}

