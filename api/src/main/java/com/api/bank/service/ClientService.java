package com.api.bank.service;

import com.api.bank.model.entity.Client;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService extends GenericService<Client, GenericRepository<Client>> {
    @Autowired
    private ClientRepository clientRepository;

    public ClientService(ClientRepository repository) {
        super(repository);
        clientRepository = repository;
    }

}
