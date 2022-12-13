package com.api.transaction.repository;

import com.api.transaction.model.TransactionRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRequestRepository extends CrudRepository<TransactionRequest, String> {
}