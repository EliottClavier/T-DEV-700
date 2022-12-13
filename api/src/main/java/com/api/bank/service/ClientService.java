package com.api.bank.service;

import com.api.bank.model.entity.Client;
import com.api.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService extends GenericService<Client, ClientRepository> {

//    @Autowired
    public ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {

        super(clientRepository);
        this.clientRepository = clientRepository;}

    public Client getClientByOrganisationName(String name){
        return clientRepository.findClientByOrganisationName(name);
    }
}
