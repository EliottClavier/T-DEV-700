package com.api.bank;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class AccountTest implements BeforeAllCallback, ExtensionContext.Store.CloseableResource{
    public static boolean started = false;

    @Autowired
    private AccountService accountService;
    private Account individualAccount;


    @Test
    void testCreateOneAccount() {

        //Arrange
        ObjectResponse res = null;
        var individualClient = new Client(UUID.randomUUID(), UUID.randomUUID().toString(), TestConstant.CLIENT_USERNAME, SocialReasonStatus.INDIVIDUAL);
        var card = new Card(UUID.randomUUID().toString());
        individualAccount = new Account(100000, individualClient, card);

        //Act

        res = accountService.add(individualAccount);

        //Assert

        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());
        assertEquals(((Account) res.getData()).getClient().getId(), individualClient.getId());
        assertEquals(((Account) res.getData()).getSold(), 100000);
        assertEquals(((Account) res.getData()).getCard().getCardId(), card.getCardId());
        individualAccount = (Account) res.getData();
    }

    @Test
    @After("testCreateOneAccount()")
    void testUpdate() {
        //Arrange
        individualAccount.getClient().setLastname("TestNewUsername");
        individualAccount.setSold(200000);
        individualAccount.getCard().setExpirationDate(new Date("2050-01-01T12:00:00+01:00"));

        //Act
        ObjectResponse res = accountService.update(individualAccount);

        //Assert
        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());
        assertEquals(((Account) res.getData()).getClient().getLastname(), "TestNewUsername");
        assertEquals(((Account) res.getData()).getSold(), 200000);
        assertEquals(((Account) res.getData()).getCard().getExpirationDate(), new Date("2050-01-01T12:00:00+01:00"));

    }

    @Test
    @After("testUpdate")
    void testDelete() {
        //Arrange
        ObjectResponse res = null;

        //Act
        res = accountService.delete(individualAccount);

        //Assert
        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());


    }

    @Test
    void testGetRealBankAccountById() {
        //Arrange
        var id = UUID.fromString(BankConstants.BANK_ID);
        //Act
        var testBankAccount = accountService.getAccountByClientId(id);

        assertEquals(Account.class, testBankAccount.getClass());
        assertEquals(id, testBankAccount.getId());
        assertEquals(BankConstants.BANK_NAME, testBankAccount.getClient().getOrganisationName());
        assertEquals(SocialReasonStatus.COMPANY, testBankAccount.getClient().getSocialReason());
    }
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!started) {
            started = true;
        }
    }

    @Override
    public void close() throws Throwable {
        if (started) {
            started = false;
           accountService.delete(individualAccount);
        }
    }
}