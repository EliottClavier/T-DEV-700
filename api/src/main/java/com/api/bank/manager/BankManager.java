package com.api.bank.manager;

import com.api.bank.model.BankConstants;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Operation;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.model.enums.OperationType;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CheckRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import com.api.bank.service.ClientService;
import com.api.bank.service.OperationService;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Component()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BankManager implements IBankManager {
    private final AccountService accountService;
    private final OperationService operationService;
    private final ClientService clientService;
    private final CheckService checkService;
    private Account withdrawAccount;
    private Account depositAccount;

    @Autowired
    private DataSource dataSource;

    private Queue<BankTransaction> transactionQueue;

    @Autowired
    public BankManager(OperationRepository operationRepository, AccountRepository accountRepository, ClientRepository clientRepository, CheckRepository checkRepository) {
        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
        this.clientService = new ClientService(clientRepository);
        this.checkService = new CheckService(checkRepository);
    }

    /**
     * Handle a transaction between two accounts
     *
     * @param transaction Represents the transaction to be processed
     * @return The transaction status by the TransactionResult Object
     */
    @Override
    @Transactional(rollbackFor = {BankTransactionException.class, Exception.class}, propagation = Propagation.REQUIRES_NEW)
    public TransactionResult HandleTransaction(BankTransaction transaction) {

        try {
            withdrawAccount = getWithdrawAccountBy(transaction);

            checkAccount(withdrawAccount, transaction, OperationType.WITHDRAW);
            checkMeansOfPayment(withdrawAccount, transaction);

            isAlreadyPendingOperation(withdrawAccount, transaction);
            var withdrawOperation = writeOperation(withdrawAccount, transaction, OperationStatus.PENDING, OperationType.WITHDRAW);  // Add the operation to the list of pending operations

            checkBalance(withdrawAccount, transaction);
            updateBalanceAndOperation(withdrawAccount, transaction, withdrawOperation);

            depositAccount = getDepositAccountBy(transaction);
            checkAccount(depositAccount, transaction, OperationType.DEPOSIT);
            var depositOperation = writeOperation(depositAccount, transaction, OperationStatus.PENDING, OperationType.DEPOSIT);

            updateBalanceAndOperation(depositAccount, transaction, depositOperation);

                return new TransactionResult(TransactionStatus.SUCCESS, transaction.getOperationId(), "Payment has been validated");

        } catch (BankTransactionException e) {
            cancelPendingOperation(transaction);
            return new TransactionResult(e.getTransactionStatus(), transaction.getOperationId(), e.getMessage());

        } catch (Exception e) {
            cancelPendingOperation(transaction);
            return new TransactionResult(TransactionStatus.FAILED, transaction.getOperationId(), e.getMessage());
        }
    }

    /**
     * Cancel a pending operation when transaction failed
     * @param transaction The transaction to be canceled
     */
    private void cancelPendingOperation(BankTransaction transaction) {
        try {
            List<UUID> accounts = new ArrayList<>();
            accounts.add(withdrawAccount.getId());
            accounts.add(depositAccount.getId());
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


    /**
     * Proceed to balance operation and update new value of the account and operation
     *
     * @param account     The account to be updated
     * @param transaction The transaction to be processed
     * @param operation   The operation to be updated
     * @throws BankTransactionException If the operation is not valid
     */
    private void updateBalanceAndOperation(Account account, BankTransaction transaction, Operation operation) throws BankTransactionException {

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
     * Check if the account or/and the qrCheck has enough balance to proceed the transaction
     *
     * @param withdrawAccount The account to be checked
     * @param transaction     The transaction to be processed
     * @throws BankTransactionException If the account or qrCheck has not enough balance
     */
    private void checkBalance(Account withdrawAccount, BankTransaction transaction) throws BankTransactionException {
        if (!withdrawAccount.isEnoughMoney(transaction.getAmount()))
            throw new BankTransactionException(TransactionStatus.INSUFFICIENT_FUNDS_ERROR, transaction.getOperationId(), "Account's insufficient funds");

        if (getCheck(transaction) != null && !getCheck(transaction).isEnoughMoney(transaction.getAmount()))
            throw new BankTransactionException(TransactionStatus.INSUFFICIENT_FUNDS_ERROR, transaction.getOperationId(), "Check amount invalid");
    }

    /**
     * Write a transaction operation and persist it in the database
     *
     * @param account     The relevant bank account
     * @param transaction The transaction to be processed
     * @param opStatus    The operation type to be persisted
     * @param opType      The operation type to be persisted
     * @throws BankTransactionException If the operation is not valid
     */
    private Operation writeOperation(Account account, BankTransaction transaction, OperationStatus opStatus, OperationType opType) throws BankTransactionException {
        var operation = createOperation(transaction, account, getCheck(transaction), opStatus, opType, transaction.getPaymentMethod());
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
    private void isAlreadyPendingOperation(Account withdrawAccount, BankTransaction transaction) throws BankTransactionException {
        // Is an operation is already in progress ?
        if (operationService.isOperationPendingFor(withdrawAccount.getId()))
            throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");
    }

    /**
     * Supply the QrCheck used for the transaction
     *
     * @param transaction The transaction to be processed
     * @return The QrCheck object
     */
    private QrCheck getCheck(BankTransaction transaction) {
        return checkService.getCheckByCheckToken(transaction.getMeansOfPaymentId());
    }

    /**
     * Check if the payment method like Card or QrCheck exist and is valid
     *
     * @param withdrawAccount the account to be debited
     * @param transaction     the transaction to be processed
     * @throws BankTransactionException if the payment method is not valid
     */
    private void checkMeansOfPayment(Account withdrawAccount, BankTransaction transaction) throws BankTransactionException {

        if (isCardPayment(transaction)) {
            // Is the card exist ?
            if (withdrawAccount.getCard() == null) {
                throw new BankTransactionException(TransactionStatus.CARD_ERROR, transaction.getOperationId(), "Card not found");
            }
            // Is the expiration date card's valid ?
            if (withdrawAccount.getCard().isExpired())
                throw new BankTransactionException(TransactionStatus.VALIDITY_DATE_ERROR, transaction.getOperationId(), "Card expired");
        } else if (isCheckPayment(transaction)) {
            var qrCheck = getCheck(transaction);
            // Is the check exist ?
            if (qrCheck == null)
                throw new BankTransactionException(TransactionStatus.CHECK_ERROR, transaction.getOperationId(), "Check not found");
            // Is the check expired ?
            if (qrCheck.isExpired())
                throw new BankTransactionException(TransactionStatus.VALIDITY_DATE_ERROR, transaction.getOperationId(), "Check expired");
        }
    }

    /**
     * Check if the account is valid
     *
     * @param transaction Represents the transaction to be processed
     * @param account     Represents the account to be debited
     * @throws BankTransactionException if the account is not valid
     */
    private void checkAccount(Account account, BankTransaction transaction, @Nullable OperationType opType) throws BankTransactionException {

        if (account == null) {
            throw new BankTransactionException(TransactionStatus.ACCOUNT_ERROR, transaction.getOperationId(), "Account not found");
        }
        if (opType == OperationType.WITHDRAW && isCheckPayment(transaction) && !account.getClient().getOrganisationName().equals(BankConstants.BANK_NAME)) {
            throw new BankTransactionException(TransactionStatus.BANK_ERROR, transaction.getOperationId(), "Bank not found");
        }
    }

    /**
     * Supply the account to withdraw
     *
     * @param transaction Represents the transaction to be processed
     * @throws BankTransactionException if the means of payment is not valid
     */
    private Account getWithdrawAccountBy(BankTransaction transaction) throws BankTransactionException {

        if (isCardPayment(transaction)) {
            return accountService.getAccountByCardId(transaction.getMeansOfPaymentId());

        } else if (isCheckPayment(transaction)) {
            return clientService.getClientByOrganisationName(BankConstants.BANK_NAME).getAccount();
        } else {
            throw new BankTransactionException(TransactionStatus.MEANS_OF_PAYMENT_ERROR, transaction.getOperationId(), "Means of Payment error was occurred");
        }
    }

    /**
     * Supply the account to deposit
     *
     * @param transaction Represents the transaction to be processed
     * @return The account to deposit
     */
    private Account getDepositAccountBy(BankTransaction transaction) {
        return accountService.getAccountByOwnerName(transaction.getDepositUsername());
    }

    /**
     * Check if the transaction is a card payment
     *
     * @param transaction Represents the transaction to be processed
     * @return true if the transaction is a card payment
     */
    private boolean isCardPayment(BankTransaction transaction) throws BankTransactionException {
        return transaction.getPaymentMethod() == PaymentMethod.CARD;
    }

    /**
     * Check if the transaction is a check payment
     *
     * @param transaction Represents the transaction to be processed
     * @return true if the transaction is a check payment
     */
    private boolean isCheckPayment(BankTransaction transaction) throws BankTransactionException {
        return transaction.getPaymentMethod() == PaymentMethod.CHECK;
    }

    /**
     * Create a new operation
     *
     * @param transaction Represents the transaction to be processed
     * @param account     Represents the account to be concerned
     * @param qrCheck     Represents the check to be debited
     * @param opStatus    Represents the operation status
     * @param opType      Represents the operation type
     * @param payMethod   Represents the payment method type
     * @return The new operation
     */
    private @NotNull Operation createOperation(BankTransaction transaction, Account account, @Nullable QrCheck qrCheck, OperationStatus opStatus, OperationType opType, PaymentMethod payMethod) {
        return new Operation(transaction.getOperationId(), transaction.getLabel(), transaction.getAmount(),
                transaction.getDate(), account, qrCheck, opStatus, opType, payMethod);
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


}