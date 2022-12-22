package com.api.bank.manager;

import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.ShoppingTransactionModel;
import com.api.bank.model.transaction.TransactionResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

public interface IBankManager {
    @Transactional()
    TransactionResult shoppingTransaction(ShoppingTransactionModel shoppingTransaction);
    @Transactional()
    TransactionResult buyCheckTransaction(QrCheckTransactionModel transaction);
}
