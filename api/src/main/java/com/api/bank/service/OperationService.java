package com.api.bank.service;

import com.api.bank.model.entity.Operation;
import com.api.bank.model.entity.OperationStatus;
import com.api.bank.repository.GenericRepository;
import com.api.bank.repository.OperationRepository;

public class OperationService extends GenericService<Operation, GenericRepository<Operation>> {

    private final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        super(operationRepository);
        this.operationRepository = operationRepository;
    }

    public boolean isOtherOperationIsPending(String operationId) {
        return operationRepository.existsAllByOperationStatusIsContainingAndOperationIdIsNot(String.valueOf(OperationStatus.PENDING), operationId);
    }
}

