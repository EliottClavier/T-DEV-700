package com.api.bank.controller;

import com.api.bank.model.entity.Account;
import com.api.bank.model.ObjectResponse;
import com.api.bank.repository.AccountRepository;
import com.api.bank.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/bank/account")
public class AccountController {

    @Autowired
    private GenericService<Account, AccountRepository> accountService;

    @GetMapping("/{id}")
    public ResponseEntity<ObjectResponse> get(@PathVariable String id){
        return ResponseEntity.ok(accountService.get(UUID.fromString(id)));
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody Account data){
        return ResponseEntity.ok(accountService.add(data));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ObjectResponse> update(@RequestBody Account data, @PathVariable String id){
        return ResponseEntity.ok(accountService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectResponse> delete(@RequestBody Account entity){
        return ResponseEntity.ok(accountService.delete(entity));
    }
}

