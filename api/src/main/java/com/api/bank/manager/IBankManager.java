package com.api.bank.manager;

import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

public interface IBankManager {
    @Transactional()
    TransactionResult handleTransaction(BankTransaction transaction) throws ExecutionException, InterruptedException;
    @Transactional()
    TransactionResult executeTransaction(BankTransaction transaction) throws BankTransactionException;


}
