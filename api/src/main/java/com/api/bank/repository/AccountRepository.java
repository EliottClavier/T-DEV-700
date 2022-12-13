package com.api.bank.repository;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Client;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface AccountRepository extends GenericRepository<Account> {

    Account findAccountByCard_CardId(String cardId);

    Account findAccountByClient(Client client);

    Account findAccountByClient_Id(UUID clientId);




}