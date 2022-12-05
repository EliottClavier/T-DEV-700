package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.ClientRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/bank/client")
public class ClientController {

//    @Autowired
    private ClientService clientService;

    public ClientController( ClientRepository clientrepo) {
        super();
        this.clientService = new ClientService(clientrepo);
    }

    @GetMapping("/{id}")
//    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
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
        return ResponseEntity.ok(clientService.delete(data));
    }

}
