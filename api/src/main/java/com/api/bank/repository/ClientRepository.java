package com.api.bank.repository;

import com.api.bank.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional

public interface ClientRepository extends GenericRepository<Client>{

}
