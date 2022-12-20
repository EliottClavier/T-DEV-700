package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;

public interface IQrCheckManager {
    QrCheck buyQrCheck(QrCheck qrCheck);
    boolean checkQrCheck(QrCheck qrCheck);
    boolean deleteQrCheck(QrCheck qrCheck);
}

