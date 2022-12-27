package com.api.bank.service;

import com.api.bank.model.entity.Client;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService extends GenericService<Client> {
    public ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository repository) {
        super(repository);
        clientRepository = repository;
    }

    public Client getClientByOrganisationName(String name){
        return clientRepository.findClientByOrganisationName(name);
    }
}
