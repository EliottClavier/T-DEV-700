package com.api.bank;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class AccountTest implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    public static boolean started = false;

    @Autowired
    private AccountService accountService;



    public final static UUID ACCOUNT_ID = UUID.fromString("19ce4597-642a-432c-aac1-e26dcfaaa8d9");

    @Test
    void testCreateOneAccount() {

        //Arrange
        ObjectResponse res = null;
        var individualClient = new Client(UUID.randomUUID(), UUID.randomUUID().toString(), TestConstant.CLIENT_USERNAME, SocialReasonStatus.INDIVIDUAL);
        var card = new Card(UUID.randomUUID().toString());
        var individualAccount = new Account(ACCOUNT_ID, 100000, individualClient, card);

        //Act

        res = accountService.add(individualAccount);

        //Assert

        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());
        assertEquals(((Account) res.getData()).getClient().getId(), individualClient.getId());
        assertEquals(((Account) res.getData()).getSold(), 100000);
        assertEquals(((Account) res.getData()).getCard().getCardId(), card.getCardId());

    }

    @Test
    @After("testCreateOneAccount")
    @Before("DeleteOneAccount")
    void testUpdate() {
        //Arrange
        var account = ((Account) accountService.get(ACCOUNT_ID.toString()).getData());
        account.getClient().setLastname("TestNewUsername");
        account.setSold(200000);
        account.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));

        //Act
        ObjectResponse res = accountService.update(account);

        //Assert
        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());
        assertEquals(((Account) res.getData()).getClient().getLastname(), "TestNewUsername");
        assertEquals(((Account) res.getData()).getSold(), 200000);
        assertEquals(((Account) res.getData()).getCard().getExpirationDate(), Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));

    }

    @Test
    @After("testUpdate")
    void testDelete() {
        //Arrange
        ObjectResponse res;
        var account = ((Account) accountService.get(ACCOUNT_ID.toString()).getData());
        //Act
        res = accountService.delete(account);

        //Assert
        assertNotNull(res);
        assertEquals("Success", res.getMessage());
        assertTrue(res.isValid());


    }

    @Test
    void testGetRealBankAccountById() {
        //Arrange
       Account testBankAccount;
        //Act
        testBankAccount = accountService.getAccountByOwnerName(BankConstants.BANK_NAME);

        assertEquals(Account.class, testBankAccount.getClass());
        assertEquals(BankConstants.BANK_NAME, testBankAccount.getClient().getOrganisationName());
        assertEquals(SocialReasonStatus.BANK, testBankAccount.getClient().getSocialReason());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!started) {
            started = true;
        }
        testCreateOneAccount();
    }

    @Override
    public void close() throws Throwable {
        if (started) {
            started = false;
            var account = ((Account) accountService.get(ACCOUNT_ID.toString()).getData());
//            accountService.delete(account);
        }
    }
}