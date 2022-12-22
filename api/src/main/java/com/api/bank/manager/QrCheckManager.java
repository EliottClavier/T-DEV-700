package com.api.bank.manager;

import com.api.bank.controller.AccountController;
import com.api.bank.model.BankConstants;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Operation;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.model.enums.OperationType;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransactionModel;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CheckRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import com.api.bank.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QrCheckManager implements IQrCheckManager {
    private final CheckService checkService;
    private final AccountService accountService;
    private final OperationService operationService;
    private Account bankAccount;
    private Account withdrawAccount;

    private BankTransactionManager bankTransactionManager;

    @Autowired
    public QrCheckManager(CheckRepository qrCheckRepository, AccountRepository accountRepository, OperationRepository operationRepository, BankTransactionManager bankTransactionManager) {
        this.checkService = new CheckService(qrCheckRepository);
        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
        this.bankTransactionManager = bankTransactionManager;
    }

    @Override
    @Transactional(rollbackOn = {BankTransactionException.class, Exception.class})
    public TransactionResult buyQrCheck(QrCheckTransactionModel transaction)  {
        try {
//            getBankAccount(transaction);
//            checkTransaction(transaction);

            checkToken(transaction);
            bankTransactionManager.executeTransaction(new BankTransactionModel(transaction));
            var depositOperation = writeOperation(bankAccount, transaction, OperationStatus.PENDING, OperationType.DEPOSIT, "Qrcheck N°", PaymentMethod.TRANSFER);

            updateBalanceAndOperation(bankAccount, transaction, depositOperation);

            createQrCheck(transaction, depositOperation);

            return new TransactionResult(TransactionStatus.SUCCESS, transaction.getOperationId(), "QrCheck successfully created");

        } catch (BankTransactionException e) {
            cancelPendingOperation(transaction);
            return new TransactionResult(e.getTransactionStatus(), transaction.getOperationId(), e.getMessage());

        } catch (Exception e) {
            cancelPendingOperation(transaction);
            return new TransactionResult(TransactionStatus.FAILED, transaction.getOperationId(), e.getMessage());
        }
    }

    private void createQrCheck(QrCheckTransactionModel transaction, Operation depositOperation) throws BankTransactionException {

        var qrCheck = new QrCheck(transaction.getAmount(), transaction.getToken());
            if(this.checkService.add(qrCheck).isValid()){
                throw new BankTransactionException(TransactionStatus.FAILED,transaction.getOperationId(), "QrCheck creation failed");
            }
    }

    private void checkToken(QrCheckTransactionModel transaction) throws BankTransactionException {
        if (transaction.getToken().isEmpty()) {
            throw new BankTransactionException(TransactionStatus.TOKEN_EMPTY_ERROR, transaction.getOperationId(), "Token is empty");
        }
        if (checkService.existsCheckByCheckToken(transaction.getToken())) {
            throw new BankTransactionException(TransactionStatus.TOKEN_ERROR, transaction.getOperationId(), "Token is already used");
        }
    }



    private void getBankAccount(QrCheckTransactionModel transaction) throws BankTransactionException {
        bankAccount = accountService.getAccountByOwnerName(BankConstants.BANK_NAME);
        if (bankAccount == null) {
            throw new BankTransactionException(TransactionStatus.BANK_ERROR, transaction.getOperationId(), " bank account not found");
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
    @Transactional(rollbackOn = {BankTransactionException.class, Exception.class})
    public void updateQrCheck(BankTransactionModel transaction) throws BankTransactionException {
        transaction.getQrCheck().setSoldAmount( transaction.getQrCheck().getSoldAmount() -  transaction.getAmount());
        if(!checkService.update(transaction.getQrCheck()).isValid()){
            throw new BankTransactionException(TransactionStatus.CHECK_ERROR,transaction.getOperationId(), "QrCheck update failed");
        }
    }

    /**
     * Proceed to balance operation and update new value of the account and operation
     *
     * @param account     The account to be updated
     * @param transaction The transaction to be processed
     * @param operation   The operation to be updated
     * @throws BankTransactionException If the operation is not valid
     */
    private void updateBalanceAndOperation(Account account, QrCheckTransactionModel transaction, Operation operation) throws BankTransactionException {

        if (operation.getOperationType() == OperationType.DEPOSIT)
            account.setSold(account.getSold() + transaction.getAmount());

        else if (operation.getOperationType() == OperationType.WITHDRAW)
            account.setSold(account.getSold() - transaction.getAmount());

        var result = accountService.update(account);
        if (result.isValid()) {
            if (!this.updateOperationStatus(OperationStatus.CLOSED, operation))
                throw new BankTransactionException(TransactionStatus.OPERATION_CLOSING_ERROR, transaction.getOperationId(), "Operation closing error");
        } else {
            this.updateOperationStatus(OperationStatus.CANCELED, operation);
            throw new BankTransactionException(TransactionStatus.PAYMENT_ERROR, transaction.getOperationId(), "Payment error was occurred");
        }
    }

    /**
     * Update the status of transaction like Pending, Close or Failed
     *
     * @param status    Represents the operation status
     * @param operation Represents the operation to be updated
     */
    private boolean updateOperationStatus(OperationStatus status, Operation operation) {
        operation.setOperationStatus(status);
        return operationService.update(operation).isValid();
    }

    /**
     * Write a operation, add to an account and persist it in the database
     * @throws BankTransactionException If the persist is not valid
     */
    private Operation writeOperation(Account account, QrCheckTransactionModel transaction, OperationStatus
            opStatus, OperationType opType, String label, PaymentMethod paymentMethod) throws BankTransactionException {

        var operation = new Operation(UUID.randomUUID().toString(), label, transaction.getAmount(), new Date(), account, null, opStatus, opType, paymentMethod);

        if (!operationService.add(operation).isValid())
            throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");
        return operation;
    }

    /**
     * Check if an existing operation is already processing to the same account
     *
     * @param withdrawAccount The account to be checked
     * @param transaction     The transaction to be processed
     * @throws BankTransactionException If an existing operation is already processing
     */
    private void isAlreadyPendingOperation(Account withdrawAccount, QrCheckTransactionModel transaction) throws BankTransactionException {
        // Is an operation is already in progress ?
        if (operationService.isOperationPendingFor(withdrawAccount.getId()))
            throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");
    }
    /**
     * Cancel a pending operation when transaction failed
     * @param transaction The transaction to be canceled
     */
    private void cancelPendingOperation(QrCheckTransactionModel transaction) {
        try {
            List<UUID> accounts = new ArrayList<>();
            accounts.add(bankAccount.getId());
//            accounts.add(depositAccount.getId());
            for (UUID accountId : accounts) {

                if (operationService.isOperationPendingByOperationId(transaction.getOperationId(), accountId)) {
                    Operation op = operationService.getByOperationId(transaction.getOperationId());
                    op.setOperationStatus(OperationStatus.CANCELED);
                    operationService.update(op);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
