package com.api.bank.controller;

import com.api.bank.manager.BankManager;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.service.AccountService;
import com.api.transaction.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/transaction")
public class TransactionController {
//    @Autowired
    private AccountService accountService;

    private BankManager bankanager;

    public TransactionController() {
//        bankanager = BankManager.getInstance();
        bankanager = new BankManager();
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionResult> add(@RequestBody BankTransaction data) {
        var transactionResult = bankanager.HandleTransaction(data);
//        return ResponseEntity.ok(accountService.add(data));
        return ResponseEntity.ok(transactionResult);
    }


}
