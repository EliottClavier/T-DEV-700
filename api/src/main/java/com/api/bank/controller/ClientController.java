package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Client;
import com.api.bank.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        super();
        this.clientService = clientService;
    }

    /**
     * Get all Client from the database
     *
     * @return ResponseEntity containing the list of Client
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getAllClient() {
        ObjectResponse response = clientService.getAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectResponse> get(@PathVariable String id) {
        return ResponseEntity.ok(clientService.get(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody Client data) {
        return ResponseEntity.ok(clientService.add(data));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ObjectResponse> update(@RequestBody Client data, @PathVariable String id) {
        return ResponseEntity.ok(clientService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectResponse> delete(@RequestBody Client data) {
        return ResponseEntity.ok(clientService.delete(data));
    }

}
