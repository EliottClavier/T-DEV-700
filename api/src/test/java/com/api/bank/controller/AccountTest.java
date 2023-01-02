package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;

import com.api.bank.repository.AccountRepository;
import com.api.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountTest {


    private final AccountService accountService;

    @Autowired
    public AccountTest(AccountRepository accountRepository) {
        accountService = new AccountService(accountRepository);
    }


    @Test
    void testCreate() {
//        //Arrange
//        ObjectResponse res1 = null;
//        ObjectResponse res2 = null;
//        ObjectResponse res3 = null;
//
//        var individualClient = new Client(UUID.randomUUID(),"test", "test", SocialReasonStatus.INDIVIDUAL);
//        var shopClient = new Client(UUID.randomUUID(),"testShop", SocialReasonStatus.INDIVIDUAL);
//        var bankClient = new Client(UUID.randomUUID(),"testBank", SocialReasonStatus.BANK);
//
//        var card = new Card();
//
//        var individualAccount = new Account(10000, individualClient, card);
//        var shopAccount = new Account(0, shopClient);
//        var bankAccount = new Account(100000, bankClient);
//
//        //Act
//        if(accountService.getAccountByOwnerName("test") == null) {
//             res1 = accountService.add(individualAccount);
//        }
//        if( accountService.getAccountByOwnerName("testShop") == null) {
//             res2 = accountService.add(shopAccount);
//        }
//        if(    accountService.getAccountByOwnerName("testBank") == null){
//             res3 = accountService.add(bankAccount);
//        }
//
//        //Assert
//
//        assertNotNull(res1);
//        assertEquals("Success", res1.getMessage());
//        assertTrue(res1.isValid());
//
//        assertNotNull(res2);
//        assertEquals("Success", res2.getMessage());
//        assertTrue(res2.isValid());
//
//        assertNotNull(res3);
//        assertEquals("Success", res3.getMessage());
//        assertTrue(res3.isValid());
    }

    @Test
    void testGetAccountById() {
//        //Arrange
//        var id = "0fa04e93-6b37-475d-ad72-d2917ea42fb0";
//        //Act
//        var objectResponse = accountService.get(id);
//
//        //Assert
//        assertEquals("Success", objectResponse.getMessage());
//        assertTrue(objectResponse.isValid());
//        assertEquals(Account.class, objectResponse.getData().getClass());
//        assertEquals(id, ((Account) objectResponse.getData()).getId().toString());
//        assertEquals("James", ((Account) objectResponse.getData()).getClient().getFirstname());
//        assertEquals("Doe", ((Account) objectResponse.getData()).getClient().getLastname());
//        assertEquals(SocialReasonStatus.INDIVIDUAL, ((Account) objectResponse.getData()).getClient().getSocialReason());

    }


    @Test
    void testAdd() {
    }

    @Test
    void testUpdate() {
    }

    @Test
    void testDelete() {
    }
}