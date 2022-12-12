package com.api.bank.repository;

import com.api.bank.model.entity.Operation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OperationRepository extends GenericRepository<Operation> {

    boolean existsAllByOperationStatusIsContainingAndOperationIdIsNot(String operationStatus, String operationId);
}
