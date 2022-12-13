package com.api.bank.service;

import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.repository.CardRepository;
import com.api.bank.repository.CheckRepository;


public class CheckService extends GenericService<QrCheck, CheckRepository> {

    public CheckRepository checkRepository;

    public CheckService(CheckRepository checkRepository) {
        super(checkRepository);
        this.checkRepository = checkRepository;
    }
    public QrCheck getCheckByCheckToken(String checkToken) {
        return this.checkRepository.findQrCheckByCheckToken(checkToken);
    }
}
