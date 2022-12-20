package com.api.bank.controller;

import com.api.bank.manager.BankManager;
import com.api.bank.manager.IBankManager;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CheckRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/transaction")
public class TransactionController {
    private final IBankManager bankManager;

    @Autowired
    public TransactionController(IBankManager bankManager) {
        super();
        this.bankManager = bankManager;
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionResult> add(@RequestBody BankTransaction data) {
        try{
            return ResponseEntity.ok(bankManager.HandleTransaction(data));
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
