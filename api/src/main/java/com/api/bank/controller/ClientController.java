package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ObjectResponse> get(@PathVariable String id){
        return ResponseEntity.ok(clientService.get(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody Client data){
        return ResponseEntity.ok(clientService.add(data));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ObjectResponse> update(@RequestBody Client data, @PathVariable String id){
        return ResponseEntity.ok(clientService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectResponse> delete(@RequestBody Client data){
        return ResponseEntity.ok(clientService.remove(data));
    }

}
