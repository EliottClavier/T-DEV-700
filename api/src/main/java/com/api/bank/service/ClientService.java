package com.api.bank.service;

import com.api.bank.model.entity.Client;
import com.api.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService extends BaseService<Client, ClientRepository> {
    @Autowired
    private ClientRepository clientRepository;

}
