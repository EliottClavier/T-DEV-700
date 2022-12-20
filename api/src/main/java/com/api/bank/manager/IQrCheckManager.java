package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.transaction.QrCheckTransaction;
import com.api.bank.model.transaction.TransactionResult;

public interface IQrCheckManager {
    TransactionResult buyQrCheck(QrCheckTransaction qrCheckTransaction);
    boolean checkQrCheck(QrCheck qrCheck);
    boolean deleteQrCheck(QrCheck qrCheck);
}

