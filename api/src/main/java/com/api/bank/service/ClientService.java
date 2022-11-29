package com.api.bank.service;

import com.api.bank.model.Client;
import com.api.bank.repository.IClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientService {
    @Autowired
    private IClientRepository clientRepository;

    public void register(Client client) {
        System.out.println("register");
        clientRepository.save(client);
        clientRepository.flush();
    }
    public void remove(Client client) {
        System.out.println("remove");
        clientRepository.delete(client);
        clientRepository.flush();
    }
    public void update() {
        System.out.println("update");
    }
}
