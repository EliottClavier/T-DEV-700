package com.api.bank.manager;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.OperationStatus;
import com.api.bank.model.transaction.CardTransaction;
import com.api.bank.model.transaction.CheckTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.bank.model.transaction.TransactionStatus;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CardRepository;
import com.api.bank.repository.GenericRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.CardService;
import com.api.bank.service.GenericService;
import com.api.bank.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class BankManager
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final AccountService accountService;
    private final OperationService operationService;



    public BankManager(OperationRepository operationRepository, AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;

        this.accountService = new AccountService(accountRepository);
        this.operationService = new OperationService(operationRepository);
    }

    public TransactionResult HandleCardOperation(CardTransaction transaction) {

        // TODO Ecrire en base qu'une operation est en cours pour le compte debiteur

        var clientAccount = accountService.getAccountByCardId(transaction.cardId);

        if (clientAccount.getCard() == null)
            return new TransactionResult(TransactionStatus.CARD_ERROR, transaction.OperationId, "Card not found");

        if (clientAccount.getCard().getExpirationDate().before(new Date()))
            return new TransactionResult(TransactionStatus.CARD_ERROR, transaction.OperationId, "Card expired");

        if (clientAccount.getSold() < transaction.getAmount())
            return new TransactionResult(TransactionStatus.INSUFFICIENT_FUNDS_ERROR, transaction.OperationId, "Insufficient funds");

        if( operationService.isOtherOperationIsPending(transaction.OperationId))
            return new TransactionResult(TransactionStatus.OPERATION_PENDING_ERROR, transaction.OperationId, "Operation pending");

        //TODO Completer la transaction debiteur
        //TODO Ecrire en base qu'une operation est terminée pour le compte debiteur

        //TODO Ecrire en base qu'une operation est en cours pour le compte crediteur
        //TODO Ecrire la transaction crediteur
        //TODO Ecrire en base qu'une operation est terminée pour le compte crediteur

        //TODO Ecrire en base le solde mis à jour pour le compte debiteur
        //TODO Ecrire en base le solde mis à jour pour le compte crediteur

        return new TransactionResult(TransactionStatus.SUCCESS, transaction.OperationId, "Transaction success");

    }

    public TransactionResult HandleCheckOperation() {
        //TODO
        return null;
    }
}
