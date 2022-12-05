package com.api.bank.controller;

import com.api.bank.model.entity.Account;
import com.api.bank.model.ObjectResponse;
import com.api.bank.repository.AccountRepository;
import com.api.bank.service.BaseService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/account")
public class AccountController {

    @Autowired
    private BaseService<Account, AccountRepository> accountService;

    @GetMapping("/{id}")
    public ResponseEntity<ObjectResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.get(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody Account data){
        return ResponseEntity.ok(accountService.add(data));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ObjectResponse> update(@RequestBody Account data, @PathVariable UUID id){
        return ResponseEntity.ok(accountService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectResponse> delete(@RequestBody Account data){
        return ResponseEntity.ok(accountService.remove(data));
    }
}


