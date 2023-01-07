package com.api.bank;

import com.api.bank.model.BankConstants;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.service.AccountService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountTests {

    @Autowired
    private AccountService accountService;
    public static boolean started = false;
    private final static String SUCCESS = "Success";
    public final static UUID ACCOUNT_ID = UUID.fromString("19ce4597-642a-432c-aac1-e26dcfaaa8d9");
    public final static UUID CLIENT_ID = UUID.fromString("20ce4597-642a-432c-aac1-e26dcfaaa8d8");
    public final static UUID CARD_ID = UUID.fromString("11ce4597-642a-432c-aac1-e26dcfaaa8d7");

    //Create a new account to testing
    @BeforeAll
    void setupAccount() {

        ObjectResponse res = null;
        var individualClient = new Client(CLIENT_ID, UUID.randomUUID().toString(), TestConst.CLIENT_USERNAME, SocialReasonStatus.INDIVIDUAL);
        var card = new Card(CARD_ID.toString());
        var individualAccount = new Account(ACCOUNT_ID, 100000, individualClient, card);

        accountService.add(individualAccount);
    }

    //Get the account created and check if the account, client and card is the same
    @Test
    void testAccountCreation() {

        //Arrange
        ObjectResponse res = null;

        //Act
        res = accountService.get(ACCOUNT_ID.toString());

        //Assert

        assertNotNull(res);
        assertEquals(SUCCESS, res.getMessage());
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(ACCOUNT_ID, ((Account) res.getData()).getId());
        assertEquals( CLIENT_ID, ((Account) res.getData()).getClient().getId());
        assertEquals(CARD_ID.toString(), ((Account) res.getData()).getCard().getCardId());
        assertEquals(100000, ((Account) res.getData()).getSold() );
    }

    //Update the account created and check if the account is the same
    @Test
    void testUpdateAccount() {
        //Arrange
        var account = ((Account) accountService.get(ACCOUNT_ID.toString()).getData());
        account.getClient().setLastname("TestNewUsername");
        account.setSold(200000);
        account.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));

        //Act
        ObjectResponse res = accountService.update(account);

        //Assert
        assertNotNull(res);
        assertEquals(SUCCESS, res.getMessage());
        assertTrue(res.isValid());
        assertEquals(((Account) res.getData()).getClient().getLastname(), "TestNewUsername");
        assertEquals(((Account) res.getData()).getSold(), 200000);
        assertEquals(((Account) res.getData()).getCard().getExpirationDate(), Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));
    }

    //Delete the account created and check if the account is deleted
    @Test
    @AfterAll
    void testDelete() {
        //Arrange
        ObjectResponse resDeleting = null;
        ObjectResponse resGetting = null;
        Account account = null;

        //Act
        account = ((Account) accountService.get(ACCOUNT_ID.toString()).getData());
        resDeleting = accountService.delete(account);

       //Assert
        assertNotNull(resDeleting);
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
}