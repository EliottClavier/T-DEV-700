package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransactionModel;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.TransactionResult;

public interface IQrCheckManager {
    TransactionResult buyQrCheck(QrCheckTransactionModel qrCheckTransaction);
    boolean checkQrCheck(QrCheck qrCheck);
    boolean deleteQrCheck(QrCheck qrCheck);

    void updateQrCheck(BankTransactionModel transaction) throws BankTransactionException;
}

