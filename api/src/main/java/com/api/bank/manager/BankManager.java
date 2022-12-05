package com.api.bank.manager;

import com.api.bank.model.entity.Report;

import java.util.UUID;

public class BankManager {

    public Report HandleCardOperation() {
        String res = "";
        UUID OperationId = UUID.randomUUID();
        // Check expirationDate, sold, etc
        //TODO Crediter et debiter les comptes vendeurs et crediteurs  et enregistrer en bdd

        return null;
    }

    public Report HandleCheckOperation() {
        //TODO
        return null;
    }
}
