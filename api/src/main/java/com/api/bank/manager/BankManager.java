package com.api.bank.manager;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.*;
import com.api.bank.model.exception.BankTransactionException;
import com.api.bank.model.transaction.*;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.ClientService;
import com.api.bank.service.OperationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Component
public class BankManager {
    private final AccountService accountService;
    private final OperationService operationService;
    private final ClientService clientService;

    private TransactionModel actualTransaction;


    public BankManager(OperationRepository operationRepository, AccountRepository accountRepository, ClientRepository clientRepository) {

        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
        this.clientService = new ClientService(clientRepository);
    }

    @Transactional(rollbackFor = {BankTransactionException.class, Exception.class})
    public TransactionResult HandleTransaction(TransactionModel transaction) {

        try {
            this.actualTransaction = transaction;
            Account withdrawAccount;
            if (transaction.getPaymentMethod() == PaymentMethod.CARD) {
                withdrawAccount = accountService.getAccountByCardId(transaction.getCardId());
            } else {
                withdrawAccount = clientService.getClientByOrganisationName(BankConstants.BANK_NAME).getAccount();
            }
            // Is the card valid ?
            if (withdrawAccount == null || withdrawAccount.getCard() == null)
                throw new BankTransactionException(TransactionStatus.CARD_ERROR, transaction.getOperationId(), "Card not found");

            // Is the expiration date card's valid ?
            if (withdrawAccount.getCard().getExpirationDate().before(new Date()))
                throw new BankTransactionException(TransactionStatus.CARD_ERROR, transaction.getOperationId(), "Card expired");

            // Is an operation is already in progress ?
            if (operationService.isOtherOperationIsPending(transaction.getOperationId()))
                throw new BankTransactionException(TransactionStatus.OPERATION_PENDING_ERROR, transaction.getOperationId(), "Operation pending error");

            // Add the operation to the list of pending operations
            var clientOperation = createOperation(transaction, withdrawAccount, OperationStatus.PENDING, OperationType.DEPOSIT, PaymentMethod.CARD);
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

            Account shopAccount = getShopAccountbyToken(transaction.getTokenShop());
            var shopOperation = createOperation(transaction, shopAccount, OperationStatus.PENDING, OperationType.DEPOSIT, PaymentMethod.CARD);

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

            return new TransactionResult(e.getTransactionStatus(), e.getOperationId(), e.getMessage());

        } catch (
                Exception e) {

            return new TransactionResult(TransactionStatus.FAILED, transaction.getOperationId(), e.getMessage());
        }

    }

    private Operation createOperation(TransactionModel transaction, Account account, OperationStatus opeStatus, OperationType opeType, PaymentMethod payMethod) {
        return new Operation(transaction.getOperationId(), transaction.getLabel(), transaction.getAmount(),
                transaction.getDate(), account, opeStatus, opeType, payMethod);

    }

    private Account getShopAccountbyToken(String tokenShop) {
        // String shopAccountId = JWTUtils.validateTokenAndRetrieveSubject(transaction.tokenShop);
        String shopAccountId = UUID.randomUUID().toString();
        return (Account) accountService.get(shopAccountId).getData();

    }

    private ObjectResponse setSoldAccount(Account clientAccount, TransactionModel transaction) {
        clientAccount.setSold(clientAccount.getSold() - transaction.getAmount());
        return accountService.update(clientAccount);
    }

    boolean persistOperationStatus(OperationStatus status, Operation operation) {
        operation.setOperationStatus(status);
        return operationService.update(operation).isValid();
    }


}
