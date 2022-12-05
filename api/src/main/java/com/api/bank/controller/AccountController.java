package com.api.bank.controller;

import com.api.bank.model.entity.Account;
import com.api.bank.model.ObjectResponse;
import com.api.bank.repository.AccountRepository;
import com.api.bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/account")
public class AccountController {


    private AccountService accountService;

    public AccountController(AccountRepository repository) {
        super();
        this.accountService = new AccountService(repository);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectResponse> get(@PathVariable String id) {
        return ResponseEntity.ok(accountService.get(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody Account data) {
        return ResponseEntity.ok(accountService.add(data));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ObjectResponse> update(@RequestBody Account data, @PathVariable String id) {
        return ResponseEntity.ok(accountService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectResponse> delete(@RequestBody Account data) {
        return ResponseEntity.ok(accountService.remove(data));
    }
}


