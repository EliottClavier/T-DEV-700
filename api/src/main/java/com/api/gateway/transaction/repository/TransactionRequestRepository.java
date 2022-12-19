package com.api.gateway.transaction.repository;

import com.api.gateway.transaction.model.TransactionRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRequestRepository extends CrudRepository<TransactionRequest, String> {
}