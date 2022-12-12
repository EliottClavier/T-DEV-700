package com.api.bank.controller;

import com.api.bank.manager.BankManager;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;

import com.api.bank.service.ClientService;
import com.api.bank.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/transaction")
public class TransactionController {

    private AccountService accountService;

    private OperationService operationService;

    private ClientService clientService;

    private BankManager bankanager;


    public TransactionController(AccountRepository accountRepository, OperationRepository operationRepository, ClientRepository clientRepository) {
        super();
        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
        this.clientService = new ClientService(clientRepository);
        bankanager = new BankManager( accountService,operationService, clientService);
    }
    @PostMapping("/add")
    public ResponseEntity<TransactionResult> add(@RequestBody BankTransaction data) {
        try{
            return ResponseEntity.ok(bankanager.HandleTransaction(data));
        } catch (BankTransactionException e) {
            return ResponseEntity.ok(new TransactionResult(e.getTransactionStatus(), data.getOperationId(), e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.ok(new TransactionResult(TransactionStatus.FAILED, data.getOperationId(), e.getMessage()));
        }
    }


}
