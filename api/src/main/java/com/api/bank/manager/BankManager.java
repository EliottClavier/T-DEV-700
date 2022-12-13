package com.api.bank.manager;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.*;
import com.api.bank.model.enums.OperationStatus;
import com.api.bank.model.enums.OperationType;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.*;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.ClientService;
import com.api.bank.service.OperationService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Queue;

@Component
//@ComponentScan("com.api.bank.service")
public class BankManager {

//    @Autowired
    private  AccountService accountService;
//    @Autowired
    private  OperationService operationService;
//    @Autowired
    private  ClientService clientService;


    private Queue<Transaction> transactionQueue;

    private BankManager bankManager;

    public BankManager(OperationRepository operationRepository, AccountRepository accountRepository, ClientRepository clientRepository) {

        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
        this.clientService = new ClientService(clientRepository);
    }
    public BankManager() {
        super();
    }
//    public BankManager() {
//
//        this.accountService = new AccountService(accountRepository);
//        this.operationService = new OperationService(operationRepository);
//        this.clientService = new ClientService(clientRepository);
//    }
    public BankManager(AccountService accountService, OperationService operationService, ClientService clientService) {

        this.accountService = accountService;
        this.operationService = operationService;
        this.clientService = clientService;
    }


    @Transactional(rollbackFor = {BankTransactionException.class, Exception.class}, propagation = Propagation.REQUIRES_NEW)
    public TransactionResult HandleTransaction(BankTransaction transaction) throws BankTransactionException {

        Account withdrawAccount;
        PaymentMethod paymentMethod = transaction.getPaymentMethod();
        QrCheck qrcheck = null;

        try {

            if (transaction.getPaymentMethod() == PaymentMethod.CARD) {
                withdrawAccount = accountService.getAccountByCardId(transaction.getCardId());

                // Is the card valid ?
                if (withdrawAccount == null || withdrawAccount.getCard() == null)
                    throw new BankTransactionException(TransactionStatus.CARD_ERROR, transaction.getOperationId(), "Card not found");

                // Is the expiration date card's valid ?
                if (withdrawAccount.getCard().getExpirationDate().before(new Date()))
                    throw new BankTransactionException(TransactionStatus.CARD_ERROR, transaction.getOperationId(), "Card expired");

            } else {
                withdrawAccount = clientService.getClientByOrganisationName(BankConstants.BANK_NAME).getAccount();
                //TODO qrcheck = operationService.getQrCheckByToken(transaction.getCheckToken());

                // Is the check valid ?
                if (qrcheck == null)
                    throw new BankTransactionException(TransactionStatus.CHECK_ERROR, transaction.getOperationId(), "Check not found");

                // Is the check expired ?
                if (qrcheck.getExpirationDate().before(new Date()))
                    throw new BankTransactionException(TransactionStatus.CHECK_ERROR, transaction.getOperationId(), "Check expired");

                // Is the check amount valid ?
                if (qrcheck.getSoldAmount() < transaction.getAmount())
                    throw new BankTransactionException(TransactionStatus.INSUFFICIENT_FUNDS_ERROR, transaction.getOperationId(), "Check amount invalid");
            }

            // Is an operation is already in progress ?
//            if (operationService.isOtherOperationIsPending(transaction.getOperationId()))
            if(operationService.isOperationPendingFor(withdrawAccount.getId()))
                throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");

            // Add the operation to the list of pending operations
            var clientOperation = createOperation(transaction, withdrawAccount, qrcheck, OperationStatus.PENDING, OperationType.WITHDRAW, paymentMethod);
            if (!operationService.add(clientOperation).isValid())
                throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");

            // Is the account debited has enough money ?
            if (withdrawAccount.getSold() < transaction.getAmount())
                throw new BankTransactionException(TransactionStatus.INSUFFICIENT_FUNDS_ERROR, transaction.getOperationId(), "Insufficient funds");

            // Debit the account and update the operation status
            if (setSoldAccount(withdrawAccount, transaction).isValid()) {
                this.persistOperationStatus(OperationStatus.CLOSED, clientOperation);
            } else {
                this.persistOperationStatus(OperationStatus.CANCELED, clientOperation);
                throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");
            }
            Account shopAccount = accountService.getAccountByClientId(transaction.getShopId());
            var shopOperation = createOperation(transaction, shopAccount, qrcheck, OperationStatus.PENDING, OperationType.DEPOSIT, paymentMethod);

            if (!operationService.add(shopOperation).isValid())
                throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");

            shopAccount.setSold(shopAccount.getSold() + transaction.getAmount());
            var resShop = accountService.update(shopAccount);

            if (resShop.isValid()) {

                if (!this.persistOperationStatus(OperationStatus.CLOSED, shopOperation))
                    throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");

                return new TransactionResult(TransactionStatus.SUCCESS, transaction.getOperationId(), "Payment has been validated");
            } else {

                this.persistOperationStatus(OperationStatus.CANCELED, shopOperation);
//                this.persistOperationStatus(OperationStatus.CANCELED, clientOperation);

                throw new BankTransactionException(TransactionStatus.PAYMENT_ERROR, transaction.getOperationId(), "Payment error was occurred");
            }
        } catch (
                BankTransactionException e) {

            return new TransactionResult(e.getTransactionStatus(), transaction.getOperationId(), e.getMessage());

        } catch (
                Exception e) {
            return new TransactionResult(TransactionStatus.FAILED, transaction.getOperationId(), e.getMessage());
        }
    }

//    @Contract("_, _, _, _, _, _ -> new")
    private @NotNull Operation createOperation(BankTransaction transaction, Account account, QrCheck qrCheck, OperationStatus opeStatus, OperationType opeType, PaymentMethod payMethod) {
        return new Operation(transaction.getOperationId(), transaction.getLabel(), transaction.getAmount(),
                transaction.getDate(), account, qrCheck, opeStatus, opeType, payMethod);
    }

//    private Account getShopAccountByToken(String shopId) {
//        return (Account) accountService.get(shopId).getData();
//    }

    private ObjectResponse setSoldAccount(Account clientAccount, BankTransaction transaction) {
        clientAccount.setSold(clientAccount.getSold() - transaction.getAmount());
        return accountService.update(clientAccount);
    }

    private boolean persistOperationStatus(OperationStatus status, Operation operation) {
        operation.setOperationStatus(status);
        return operationService.update(operation).isValid();
    }
}