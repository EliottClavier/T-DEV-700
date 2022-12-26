package com.api.bank.controller;

import com.api.bank.manager.IBankManager;
import com.api.bank.manager.BankManager;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.ShoppingTransactionModel;
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

    @PostMapping("/shopping")
    public ResponseEntity<TransactionResult> shopping(@RequestBody ShoppingTransactionModel data) {
        try{
            var res = transManager.shoppingTransaction(data);

            return ResponseEntity.ok(res );
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/qrcheck/purchase")
    public ResponseEntity<TransactionResult> qrCheckPurchase(@RequestBody QrCheckTransactionModel data) {
        try{
            var res = transManager.buyCheckTransaction(data);

            return ResponseEntity.ok(res );
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
