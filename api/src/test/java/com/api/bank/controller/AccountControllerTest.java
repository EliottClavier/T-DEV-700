package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import static org.junit.jupiter.api.Assertions.*;
@Component
class AccountControllerTest {

    @Autowired
    private AccountService accountService;

    @Test
    void testCreate() {
        //Arrange
        var id = "ae06d731-0ff2-43a8-aa92-ea0e0fc1f0e1";
        var client = new Client();
        var card = new Card();

        //Act
        var objectResponse = accountService.get(id);

        //Assert
        assertEquals("Success", objectResponse.getMessage());
        assertTrue(objectResponse.isValid());
        assertEquals(Account.class, objectResponse.getData().getClass());
        assertEquals(id, ((Account) objectResponse.getData()).getId().toString());
        assertEquals("James", ((Account) objectResponse.getData()).getClient().getFirstname());
        assertEquals("Doe", ((Account) objectResponse.getData()).getClient().getLastname());
        assertEquals(SocialReasonStatus.INDIVIDUAL, ((Account) objectResponse.getData()).getClient().getSocialReason());

    }

    @Test
    void testGet() {
        //Arrange
            var id = "ae06d731-0ff2-43a8-aa92-ea0e0fc1f0e1";

        //Act
        var objectResponse = accountService.get(id);

        //Assert
        assertEquals("Success", objectResponse.getMessage());
        assertTrue(objectResponse.isValid());
        assertEquals(Account.class, objectResponse.getData().getClass());
        assertEquals(id, ((Account) objectResponse.getData()).getId().toString());
        assertEquals("James", ((Account) objectResponse.getData()).getClient().getFirstname());
        assertEquals("Doe", ((Account) objectResponse.getData()).getClient().getLastname());
        assertEquals(SocialReasonStatus.INDIVIDUAL, ((Account) objectResponse.getData()).getClient().getSocialReason());

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