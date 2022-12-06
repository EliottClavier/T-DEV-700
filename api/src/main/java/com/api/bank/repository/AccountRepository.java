package com.api.bank.repository;

import com.api.bank.model.entity.Account;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AccountRepository extends GenericRepository<Account> {

    Account findAccountByCard_CardId(String accountId);




}