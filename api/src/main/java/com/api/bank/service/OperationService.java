package com.api.bank.service;

import com.api.bank.model.entity.Operation;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.repository.GenericRepository;
import com.api.bank.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OperationService extends GenericService<Operation> {

    @Autowired
    public OperationService(OperationRepository repository) {
        super(repository);
    }

    public boolean isOperationPendingFor(UUID accountId) {
        return ((OperationRepository) repository).existsOperationByOperationStatusIsLikeAndAccountIdIs(OperationStatus.PENDING, accountId);
    }
    public boolean isOperationPendingByOperationId( String operationId, UUID accountId) {
        var res = ((OperationRepository) repository).existsOperationByOperationStatusIsLikeAndOperationIdIsAndAccountIdIs(OperationStatus.PENDING, operationId, accountId);
        return res;
    }
    public Operation getByOperationId(String operationId) {
        return ((OperationRepository) repository).getByOperationId(operationId);
    }
}

