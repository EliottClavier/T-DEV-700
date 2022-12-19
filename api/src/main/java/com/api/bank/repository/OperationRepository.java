package com.api.bank.repository;

import com.api.bank.model.entity.Operation;
import com.api.bank.model.enums.OperationStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface OperationRepository extends GenericRepository<Operation> {

    boolean existsOperationByOperationStatusIsLikeAndAccountIdIs(OperationStatus operationStatus, UUID accountId);
    boolean existsOperationByOperationStatusIsLikeAndOperationIdIsAndAccountIdIs(OperationStatus operationStatus,String OperationId, UUID accountId);
    Operation getByOperationId(String operationId);
}
