package com.api.bank.manager;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Operation;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.model.enums.OperationType;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CheckRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import com.api.bank.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

@Component
public class QrCheckManager {
    private final CheckService checkService;
    private final BankManager bankManager;
    private final AccountService accountService;
    private final OperationService operationService;

    @Autowired
    public QrCheckManager(CheckRepository qrCheckRepository, AccountRepository accountRepository, OperationRepository operationRepository, BankManager bankManager) {
        this.checkService = new CheckService(qrCheckRepository);
        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);

        this.bankManager = bankManager;
    }

    @Transactional(rollbackOn = Exception.class)
    public QrCheck buyQrCheck(QrCheck qrCheck) {
        try {
            //check if the check is valid
            if (qrCheck.getCheckToken().isEmpty()) {
                throw new Exception("Invalid check");
            }
            //CHECK IF THE QRCHECK IS ALREADY IN THE DATABASE
            if (checkService.existsCheckByCheckToken(qrCheck.getCheckToken())) {
                throw new Exception("Check already exists");     //IF IT IS, THROW AN EXCEPTION
            }
            var bankAccount = accountService.getAccountByOwnerName(BankConstants.BANK_NAME);

            var res = writeOperation(bankAccount, qrCheck, OperationStatus.PENDING, OperationType.WITHDRAW, "Qrcheck Buy");

            if(res.isValid())
                this.checkService.add(qrCheck);

            return new QrCheck();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write a operation, add to an account and persist it in the database
     * @throws Exception If the persist is not valid
     */
    private ObjectResponse writeOperation(Account account, QrCheck qrCheck, OperationStatus
            opStatus, OperationType opType, String label) throws Exception {
        var operation = new Operation(UUID.randomUUID().toString(), label, qrCheck.getSoldAmount(), new Date(), account, qrCheck, opStatus, opType, null);

        account.setSold(account.getSold() - qrCheck.getSoldAmount());
        var res = accountService.update(account);

        if (!res.isValid())
            throw new Exception("Error");
        return res;
    }

}
