package com.api.bank.manager;

import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;

import javax.transaction.Transactional;

public interface IBankManager {

    TransactionResult HandleTransaction(BankTransaction transaction);
}
