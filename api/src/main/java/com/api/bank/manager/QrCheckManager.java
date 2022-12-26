package com.api.bank.manager;

import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransactionModel;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.repository.CheckRepository;
import com.api.bank.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QrCheckManager implements IQrCheckManager {
    private final CheckService checkService;

    @Autowired
    public QrCheckManager(CheckRepository qrCheckRepository) {
        this.checkService = new CheckService(qrCheckRepository);
    }

    @Override
    @Transactional(rollbackFor= {BankTransactionException.class, Exception.class}, propagation = Propagation.REQUIRED  )
    public TransactionResult buyQrCheck(QrCheckTransactionModel transaction) {
        try {

            checkToken(transaction);
            createQrCheck(transaction);

            return new TransactionResult(TransactionStatus.SUCCESS, transaction.getOperationId(), "QrCheck successfully created");

        } catch (BankTransactionException e) {
            return new TransactionResult(e.getTransactionStatus(), transaction.getOperationId(), e.getMessage());

        } catch (Exception e) {
            return new TransactionResult(TransactionStatus.FAILED, transaction.getOperationId(), e.getMessage());
        }
    }
    @Transactional(rollbackFor = {BankTransactionException.class, RuntimeException.class}, propagation = Propagation.REQUIRED)
    public TransactionResult createQrCheck(QrCheckTransactionModel transaction) throws BankTransactionException {

        var qrCheck = new QrCheck(transaction.getAmount(), transaction.getToken());
        if (!this.checkService.add(qrCheck).isValid()) {
            throw new BankTransactionException(TransactionStatus.FAILED, transaction.getOperationId(), "QrCheck creation failed");
        }
        return new TransactionResult(TransactionStatus.SUCCESS, transaction.getOperationId(), "QrCheck successfully created");
    }

    @Transactional(rollbackFor = {BankTransactionException.class, RuntimeException.class}, propagation = Propagation.REQUIRED)
    public void checkToken(QrCheckTransactionModel transaction) throws BankTransactionException {
        if (transaction.getToken().isEmpty()) {
            throw new BankTransactionException(TransactionStatus.TOKEN_EMPTY_ERROR, transaction.getOperationId(), "Token is empty");
        }
        if (checkService.existsCheckByCheckToken(transaction.getToken())) {
            throw new BankTransactionException(TransactionStatus.TOKEN_ERROR, transaction.getOperationId(), "Token is already used");
        }
    }


    @Override
    public boolean checkQrCheck(QrCheck qrCheck) {
        return false;
    }

    @Override
    public boolean deleteQrCheck(QrCheck qrCheck) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = {BankTransactionException.class, Exception.class}, propagation = Propagation.REQUIRED)
    public void updateQrCheck(BankTransactionModel transaction) throws BankTransactionException {
        transaction.getQrCheck().setSoldAmount(transaction.getQrCheck().getSoldAmount() - transaction.getAmount());
        if (!checkService.update(transaction.getQrCheck()).isValid()) {
            throw new BankTransactionException(TransactionStatus.CHECK_ERROR, transaction.getOperationId(), "QrCheck update failed");
        }
    }


}
