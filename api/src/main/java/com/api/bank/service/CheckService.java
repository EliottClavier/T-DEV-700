package com.api.bank.service;

import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.repository.CardRepository;
import com.api.bank.repository.CheckRepository;
import com.api.bank.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckService extends GenericService<QrCheck> {
//    @Autowired
    public CheckRepository checkRepository;

    @Autowired
    public CheckService(CheckRepository checkRepository) {
        super(checkRepository);
        this.checkRepository = checkRepository;
    }
    public QrCheck getCheckByCheckToken(String checkToken) {
        return this.checkRepository.findQrCheckByCheckToken(checkToken);
    }
}
