package com.api.bank.manager;

import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

import static com.api.bank.model.enums.TransactionStatus.SUCCESS;

@Component
public class BankManager {
    public BankTransactionManager bankTransactionManager;
    private final ExecutorService executor;

    @Autowired
    public BankManager(BankTransactionManager bankTransactionManager) {
        this.bankTransactionManager = bankTransactionManager;

        executor = Executors.newSingleThreadExecutor();
    }

    public TransactionResult doTransaction(BankTransaction transaction) {
        Future<TransactionResult> result = executor.submit(new Callable<TransactionResult>() {
            public TransactionResult call() throws InterruptedException {
                try {
                    return bankTransactionManager.executeTransaction(transaction);
                } catch (BankTransactionException e) {
                    throw new InterruptedException(e.getTransactionStatus().toString());
                }
            }
        });

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            return new TransactionResult(getEnum(e.getCause().getMessage()), transaction.getOperationId(), getMessage(e.getCause().getMessage()));
        }
    }

    TransactionStatus getEnum(String value) {
        try {
            return TransactionStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TransactionStatus.FAILED;
        }
    }

    String getMessage(String value) {
        try {
            return switch (TransactionStatus.valueOf(value)) {
                case FAILED -> "Transaction failed";
                case SUCCESS -> "Payment has been validated";
                case OPERATION_PENDING_ERROR -> "Operation is already pending";
                case OPERATION_CLOSING_ERROR -> "Operation is already closed";
                case PAYMENT_ERROR -> "Payment error was occurred";
                case MEANS_OF_PAYMENT_ERROR -> "Means of Payment error was occurred";
                case CARD_ERROR ->  "Card not found";
                case VALIDITY_DATE_ERROR -> "Means of payment expired";
                case CHECK_ERROR -> "Check not found";
                case TOKEN_EMPTY_ERROR, TOKEN_ERROR -> "Token error";
                case BANK_ERROR -> "Bank not found";
                case INSUFFICIENT_FUNDS_ERROR -> "Account's insufficient funds";
                case ACCOUNT_ERROR -> "Account not found";
                case EMPTY_TRANSACTION_ERROR -> "Empty transaction error";
                default -> "Transaction failed";
            };
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
}
