package com.api.bank.repository;

import com.api.bank.model.entity.Operation;

public interface OperationRepository extends GenericRepository<Operation> {

    boolean existsAllByOperationStatusIsContainingAndOperationIdIsNot(String operationStatus, String operationId);
}
