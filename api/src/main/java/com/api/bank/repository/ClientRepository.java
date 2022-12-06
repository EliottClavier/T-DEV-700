package com.api.bank.repository;

import com.api.bank.model.entity.Client;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional

public interface ClientRepository extends GenericRepository<Client>{

    Client findClientByOrganisationName(String name);

}
