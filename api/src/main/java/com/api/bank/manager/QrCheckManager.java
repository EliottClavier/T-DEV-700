package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.repository.CheckRepository;
import com.api.bank.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class QrCheckManager {
    private final CheckService checkService;
    private final BankManager bankManager;

    @Autowired
    public QrCheckManager(CheckRepository qrCheckRepository, BankManager bankManager) {
        this.checkService = new CheckService(qrCheckRepository);
        this.bankManager = bankManager;
    }

    @Transactional
    public QrCheck buyQrCheck(QrCheck check) {
        try {
            //check if the check is valid
            if (check == null || check.getCheckToken().isEmpty()) {
                throw new Exception("Invalid check");
            }
            //CHECK IF THE CHECK IS ALREADY IN THE DATABASE
            //IF IT IS, THROW AN EXCEPTION

            //CREATE NEW BANK TRANSACTION WITH THE ACCOUNT OF CLIENT
            bankManager.HandleTransaction(new BankTransaction());

            //SAVE THE CHECK IN THE DATABASE
            this.checkService.add(check);





            return new QrCheck();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
