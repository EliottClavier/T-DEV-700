package com.api.bank.manager;

import com.api.bank.model.BankConstants;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.enums.BankTransactionType;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.BankTransactionModel;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.ShoppingTransactionModel;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import com.api.bank.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BankManager implements IBankManager {
    private final BankTransactionManager bankTransactionManager;
    private final QrCheckManager qrCheckManager;
    private final ExecutorService executor;
    private final AccountService accountService;
    private final ClientService clientService;
    private final CheckService checkService;


    @Autowired
    public BankManager(BankTransactionManager bankTransactionManager,
                       AccountService accountService, ClientService clientService,
                       CheckService checkService, QrCheckManager qrCheckManager) {
        this.bankTransactionManager = bankTransactionManager;
        this.accountService = accountService;
        this.clientService = clientService;
        this.checkService = checkService;
        this.qrCheckManager = qrCheckManager;

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public TransactionResult shoppingTransaction(ShoppingTransactionModel shoppingTransaction) {
        Future<TransactionResult> result = executor.submit(new Callable<TransactionResult>() {
            @Transactional(rollbackFor = {BankTransactionException.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
            public TransactionResult call() throws InterruptedException {
                try {
                    var bankTransaction = createBankTransactionFrom(shoppingTransaction);
                     bankTransactionManager.executeTransaction(bankTransaction);

                    return new TransactionResult(TransactionStatus.SUCCESS, bankTransaction.getOperationId(), "Payment has been validated");


                } catch (BankTransactionException ex) {
                    throw new InterruptedException(ex.getTransactionStatus().toString());
                }
            }
        });
        try {

            return result.get();

        } catch (ExecutionException | InterruptedException e) {
            return new TransactionResult(getEnum(e.getCause().getMessage()), shoppingTransaction.getOperationId(), getMessage(e.getCause().getMessage()));
        }
    }

    @Override
    public TransactionResult buyCheckTransaction(QrCheckTransactionModel qrCheckTransaction) {
        Future<TransactionResult> result = executor.submit(new Callable<TransactionResult>() {
            @Transactional(rollbackFor = {BankTransactionException.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
            public TransactionResult call() throws InterruptedException {
                try {

                    var bankTransaction = createBankTransactionFrom(qrCheckTransaction);
                    qrCheckManager.checkToken(qrCheckTransaction);
                    bankTransactionManager.executeTransaction(bankTransaction);

                    return qrCheckManager.createQrCheck(qrCheckTransaction);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new InterruptedException(ex.getMessage());
                }
            }
        });

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            return new TransactionResult(getEnum(e.getCause().getMessage()), qrCheckTransaction.getOperationId(), getMessage(e.getCause().getMessage()));
        }
    }

    private BankTransactionModel createBankTransactionFrom(QrCheckTransactionModel qrCheckTransaction) {

        var bankTransaction = new BankTransactionModel(qrCheckTransaction);

        bankTransaction.setDepositAccount(getBankAccount());
        var withdrawalAccount = accountService.getAccountByClientId(qrCheckTransaction.getClientId());
        bankTransaction.setWithdrawalAccount(withdrawalAccount);

        return bankTransaction;
    }

    private BankTransactionModel createBankTransactionFrom(ShoppingTransactionModel shoppingTransaction) throws BankTransactionException {

        var bankTransaction = new BankTransactionModel(shoppingTransaction);

        bankTransaction.setDepositAccount(getDepositAccountBy(shoppingTransaction));
        bankTransaction.setWithdrawalAccount(getWithdrawAccountBy(shoppingTransaction, BankTransactionType.SHOPPING));
        bankTransaction.setQrCheck(getQrCheckFrom(shoppingTransaction));

        return bankTransaction;
    }

    /**
     * Supply the account to deposit
     *
     * @param transaction Represents the transaction to be processed
     * @return The account to deposit
     */
    private Account getDepositAccountBy(ShoppingTransactionModel transaction) {
        return accountService.getAccountByOwnerName(transaction.getDepositUsername());
    }


    /**
     * Supply the account to withdraw
     *
     * @param transaction Represents the transaction to be processed
     * @throws BankTransactionException if the means of payment is not valid
     */
    private Account getWithdrawAccountBy(ShoppingTransactionModel transaction, BankTransactionType bankTransactionType) throws BankTransactionException {

        if (isCardPayment(transaction)) {
            return accountService.getAccountByCardId(transaction.getMeansOfPaymentId());

        } else if (isCheckPayment(transaction)) {
            return clientService.getClientByOrganisationName(BankConstants.BANK_NAME).getAccount();
        } else {
            throw new BankTransactionException(TransactionStatus.MEANS_OF_PAYMENT_ERROR, transaction.getOperationId(), "Means of Payment error was occurred");
        }
    }

    /**
     * Check if the transaction is a card payment
     *
     * @param transaction Represents the transaction to be processed
     * @return true if the transaction is a card payment
     */
    private boolean isCardPayment(ShoppingTransactionModel transaction) {
        return transaction.getPaymentMethod() == PaymentMethod.CARD;
    }

    /**
     * Check if the transaction is a check payment
     *
     * @param transaction Represents the transaction to be processed
     * @return true if the transaction is a check payment
     */
    private boolean isCheckPayment(ShoppingTransactionModel transaction) {
        return transaction.getPaymentMethod() == PaymentMethod.CHECK;
    }

    private Account getBankAccount() {
        return clientService.getClientByOrganisationName(BankConstants.BANK_NAME).getAccount();
    }

    /**
     * Supply the QrCheck used for the transaction
     *
     * @param transaction The transaction to be processed
     * @return The QrCheck object
     */
    private QrCheck getQrCheckFrom(ShoppingTransactionModel transaction) {
        return checkService.getCheckByCheckToken(transaction.getMeansOfPaymentId());
    }

    TransactionStatus getEnum(String value) {
        try {
            return TransactionStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TransactionStatus.FAILED;
        }
    }

    String getMessage(String value) {
        try {
            return switch (TransactionStatus.valueOf(value)) {
                case FAILED -> "Payment has been failed";
                case SUCCESS -> "Payment has been validated";
                case OPERATION_PENDING_ERROR -> "Operation is already pending";
                case OPERATION_CLOSING_ERROR -> "Operation is already closed";
                case PAYMENT_ERROR -> "Payment error was occurred";
                case MEANS_OF_PAYMENT_ERROR -> "Means of Payment error was occurred";
                case CARD_ERROR -> "Card not found";
                case VALIDITY_DATE_ERROR -> "Means of payment expired";
                case CHECK_ERROR -> "Check not found";
                case TOKEN_EMPTY_ERROR, TOKEN_ERROR -> "Token error";
                case BANK_ERROR -> "Bank not found";
                case INSUFFICIENT_FUNDS_ERROR -> "Account's insufficient funds";
                case ACCOUNT_ERROR -> "Account not found";
                case EMPTY_TRANSACTION_ERROR -> "Empty transaction error";
                default -> "";
            };
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
}
