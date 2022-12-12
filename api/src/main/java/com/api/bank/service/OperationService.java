package com.api.bank.service;

import com.api.bank.model.entity.Operation;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.repository.GenericRepository;
import com.api.bank.repository.OperationRepository;

import java.util.UUID;

public class OperationService extends GenericService<Operation, GenericRepository<Operation>> {

    private final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        super(operationRepository);
        this.operationRepository = operationRepository;
    }

    public boolean isOperationPendingFor(UUID accountId) {
        return operationRepository.existsOperationByOperationStatusIsLikeAndAccountIdIs(OperationStatus.PENDING, accountId);
    }
}

