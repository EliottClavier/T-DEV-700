package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransactionModel;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.TransactionResult;

public interface IQrCheckManager {
    TransactionResult createQrCheck(QrCheckTransactionModel transaction) throws BankTransactionException;
    boolean infoQrCheck(QrCheck qrCheck);
    boolean deleteQrCheck(QrCheck qrCheck);

    void updateQrCheck(BankTransactionModel transaction) throws BankTransactionException;
}

