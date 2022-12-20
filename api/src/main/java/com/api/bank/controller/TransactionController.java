package com.api.bank.controller;

import com.api.bank.manager.IBankManager;
import com.api.bank.manager.BankManager;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/transaction")
public class TransactionController {
    private final IBankManager bankManager;
    private final BankManager transManager;

    @Autowired
    public TransactionController(IBankManager bankManager, BankManager transManager) {
        super();
        this.bankManager = bankManager;
        this.transManager = transManager;
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionResult> add(@RequestBody BankTransaction data) {
        try{
            var res = transManager.doTransaction(data);

            return ResponseEntity.ok(res );
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
