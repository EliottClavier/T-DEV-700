package com.api.bank;


import com.api.bank.manager.IBankManager;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.SocialReasonStatus;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.transaction.QrCheckTransactionModel;
import com.api.bank.model.transaction.ShoppingTransactionModel;
import com.api.bank.service.AccountService;
import com.api.bank.service.CheckService;
import org.aspectj.lang.annotation.After;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class BankTransactionTest implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    public static boolean started = false;

    @Autowired
    IBankManager bankManager;

    @Autowired
    AccountService accountService;

    @Autowired
    CheckService qrCheckService;


    Account individualAccount;
    Account shopAccount;
    Account bankAccount;

    @Test
    void testInitAccounts() {

        //Arrange
        ObjectResponse res1 = null;
        ObjectResponse res2 = null;
        ObjectResponse res3 = null;

        var individualClient = new Client(TestConstant.CLIENT_ID, TestConstant.CLIENT_USERNAME, TestConstant.CLIENT_USERNAME, SocialReasonStatus.INDIVIDUAL);
        var shopClient = new Client(TestConstant.SHOP_ID, TestConstant.SHOP_USERNAME, SocialReasonStatus.COMPANY);
        var bankClient = new Client(TestConstant.BANK_ID, TestConstant.BANK_USERNAME, SocialReasonStatus.BANK);

        var card = new Card(TestConstant.CARD_ID);

        individualAccount = new Account(100000, individualClient, card);
        shopAccount = new Account(0, shopClient);
        bankAccount = new Account(10000000, bankClient);

        //Act

        if (!accountService.exist(TestConstant.CLIENT_ID.toString())) {
            res1 = accountService.add(individualAccount);
        } else {
            res1 = accountService.get(TestConstant.CLIENT_ID.toString());
        }

        if (!accountService.exist(TestConstant.SHOP_ID.toString())) {
            res2 = accountService.add(shopAccount);
        } else {
            res2 = accountService.get(TestConstant.SHOP_ID.toString());
        }

        if (!accountService.exist(TestConstant.BANK_ID.toString())) {
            res3 = accountService.add(bankAccount);
        } else {
            res3 = accountService.get(TestConstant.BANK_ID.toString());
        }

        //Assert

        assertNotNull(res1);
        assertEquals("Success", res1.getMessage());
        assertEquals(HttpStatus.OK, res1.getStatus() );

        assertNotNull(res2);
        assertEquals("Success", res2.getMessage());
        assertEquals(HttpStatus.OK, res2.getStatus() );

        assertNotNull(res3);
        assertEquals("Success", res3.getMessage());
        assertEquals(HttpStatus.OK, res3.getStatus() );
    }


    @Test
    @After("testInitAccounts")
    void testCreateQrCode() {
        //Arrange

        var bankAccount = accountService.getAccountByClientId(TestConstant.BANK_ID);
        var clientAccount = accountService.getAccountByClientId(TestConstant.CLIENT_ID);

        Account bankAccountAfter;
        Account clientAccountAfter;

        var transaction = new QrCheckTransactionModel(UUID.randomUUID().toString(), TestConstant.CHECK_ID, 100, TestConstant.CLIENT_ID, PaymentMethod.TRANSFER);

        //Act
        var res = bankManager.buyCheckTransaction(transaction);
        bankAccountAfter = accountService.getAccountByClientId(TestConstant.BANK_ID);
        clientAccountAfter = accountService.getAccountByClientId(TestConstant.CLIENT_ID);
        var check = qrCheckService.get(TestConstant.CHECK_ID);

        //Assert
        assertNotNull(res);
        assertEquals(TransactionStatus.SUCCESS, res.getTransactionStatus());
        assertEquals(bankAccountAfter.getSold(), bankAccount.getSold() + 100);
        assertEquals(clientAccountAfter.getSold(), clientAccount.getSold() - 100);

        assertNotNull(check);
        assertEquals(TestConstant.CHECK_ID, ((QrCheck) check.getData()).getId().toString());
        assertEquals(100, ((QrCheck) check.getData()).getSoldAmount());
        assertEquals(365, ((QrCheck) check.getData()).getNbDayOfValidity());


    }

    @Test
    @After("testInitAccounts")
    void shoppingTransactionByCard() {

        //Arrange
        Account beforeDepositAccount = (Account) accountService.get(TestConstant.SHOP_ID.toString()).getData();
        Account beforeWithdrawAccount = (Account) accountService.get(TestConstant.CLIENT_ID.toString()).getData();

        Account afterDepositAccount = null;
        Account afterWithdrawAccount = null;

        ShoppingTransactionModel transaction = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.SHOP_USERNAME, "test", TestConstant.CARD_ID, 10, PaymentMethod.CARD);

        //Act

        var res = bankManager.shoppingTransaction(transaction);
        afterDepositAccount = (Account) accountService.get(TestConstant.SHOP_ID.toString()).getData();
        afterWithdrawAccount = (Account) accountService.get(TestConstant.CLIENT_ID.toString()).getData();

        //Assert

        assertEquals(res.getTransactionStatus(), TransactionStatus.SUCCESS);
        assertEquals(afterDepositAccount.getSold(), beforeDepositAccount.getSold() - 10);
        assertEquals(afterWithdrawAccount.getSold(), beforeWithdrawAccount.getSold() + 10);

    }

    @Test
    @After("testCreateQrCode")
    void shoppingTransactionByQrCheck() {
        //Arrange
        Account beforeDepositAccount = (Account) (accountService.get(TestConstant.SHOP_ID.toString())).getData();
        Account beforeWithdrawAccount = (Account) (accountService.get(TestConstant.BANK_ID.toString())).getData();

        Account afterDepositAccount = null;
        Account afterWithdrawAccount = null;

        QrCheck qrCheck = (QrCheck) qrCheckService.get(TestConstant.CHECK_ID).getData();
        double qrSold = qrCheck.getSoldAmount();

        ShoppingTransactionModel transaction = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.BANK_USERNAME, "test", qrCheck.getCheckToken(), 10, PaymentMethod.CHECK);

        //Act

        var res = bankManager.shoppingTransaction(transaction);
        afterDepositAccount = (Account) (accountService.get(TestConstant.SHOP_ID.toString())).getData();
        afterWithdrawAccount = (Account) (accountService.get(TestConstant.BANK_ID.toString())).getData();
        qrCheck = (QrCheck) qrCheckService.get(TestConstant.CHECK_ID).getData();
        //Assert

        assertEquals(res.getTransactionStatus(), TransactionStatus.SUCCESS);
        assertEquals(afterDepositAccount.getSold(), beforeDepositAccount.getSold() - 10);
        assertEquals(afterWithdrawAccount.getSold(), beforeWithdrawAccount.getSold() + 10);
        assertEquals(qrCheck.getSoldAmount(), qrSold - 10);

    }

    @Test
    @After("shoppingTransactionByQrCheck")
    void shoppingCardTransactionFailed() {
        //Arrange
        Account beforeDepositAccount = (Account) (accountService.get(TestConstant.SHOP_ID.toString())).getData();
        Account beforeWithdrawAccount = (Account) (accountService.get(TestConstant.CLIENT_ID.toString())).getData();

        Account afterWithdrawNotEnoughMoney = null;
        Account afterDepositNotEnoughMoney = null;
        Account afterDepositAccountNullWithdrawAccount = null;
        Account afterDepositAccountNullDepositAccount = null;

        ShoppingTransactionModel transactionNull = null;

        ShoppingTransactionModel transactionCardNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.SHOP_USERNAME, "test", "", 10, PaymentMethod.CARD);
        ShoppingTransactionModel transactionCardExpired = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.SHOP_USERNAME, "test", TestConstant.CARD_ID, 10, PaymentMethod.CARD);

        ShoppingTransactionModel transactionWithdrawalAccountNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.SHOP_USERNAME, "test", "notAValidCardId", 10, PaymentMethod.CARD);
        ShoppingTransactionModel transactionNotEnoughMoney = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConstant.SHOP_USERNAME, "test", TestConstant.CARD_ID, 1000000000, PaymentMethod.CARD);

        ShoppingTransactionModel transactionDepositAccountNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), "dede", "test", TestConstant.CARD_ID, 10, PaymentMethod.CARD);

        //Act

        var resTransactionNull = bankManager.shoppingTransaction(transactionNull);

        var resTransactionCardNull = bankManager.shoppingTransaction(transactionCardNull);

        individualAccount.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2020-02-13T18:51:09.840Z"))));
        accountService.update(individualAccount);
        var resTransactionCardExpired = bankManager.shoppingTransaction(transactionCardExpired);
        individualAccount.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));
        accountService.update(individualAccount);

        var resTransactionWithdrawalAccountNull = bankManager.shoppingTransaction(transactionWithdrawalAccountNull);

        var resTransactionNotEnoughMoney = bankManager.shoppingTransaction(transactionNotEnoughMoney);
        afterWithdrawNotEnoughMoney = (Account) (accountService.get(TestConstant.CLIENT_ID.toString())).getData();
        afterDepositNotEnoughMoney = (Account) (accountService.get(TestConstant.SHOP_ID.toString())).getData();

        var resTransactionDepositAccountNull = bankManager.shoppingTransaction(transactionDepositAccountNull);
        afterDepositAccountNullWithdrawAccount = (Account) (accountService.get(TestConstant.CLIENT_ID.toString())).getData();
        afterDepositAccountNullDepositAccount = (Account) (accountService.get(TestConstant.SHOP_ID.toString())).getData();


        //Assert

        assertEquals(resTransactionNull.getTransactionStatus(), TransactionStatus.EMPTY_TRANSACTION_ERROR);

        assertEquals(resTransactionCardNull.getTransactionStatus(), TransactionStatus.CARD_ERROR);

        assertEquals(resTransactionCardExpired.getTransactionStatus(), TransactionStatus.VALIDITY_DATE_ERROR);

        assertThat(resTransactionWithdrawalAccountNull.getTransactionStatus(), Matchers.either(Matchers.is(TransactionStatus.ACCOUNT_ERROR)).or(Matchers.is(TransactionStatus.BANK_ERROR)));

        assertEquals(resTransactionNotEnoughMoney.getTransactionStatus(), TransactionStatus.INSUFFICIENT_FUNDS_ERROR);
        assertEquals(afterWithdrawNotEnoughMoney.getSold(), beforeWithdrawAccount.getSold());
        assertEquals(afterDepositNotEnoughMoney.getSold(), beforeDepositAccount.getSold());

        assertThat(resTransactionDepositAccountNull.getTransactionStatus(), Matchers.either(Matchers.is(TransactionStatus.ACCOUNT_ERROR)).or(Matchers.is(TransactionStatus.BANK_ERROR)));
        assertEquals(afterDepositAccountNullWithdrawAccount.getSold(), beforeWithdrawAccount.getSold());
        assertEquals(afterDepositAccountNullDepositAccount.getSold(), beforeDepositAccount.getSold());

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!started) {
            started = true;
            testInitAccounts();
        }
    }

    @Override
    public void close() throws Throwable {
        started = false;
        deleteAll();

    }

    private void deleteAll() {

        qrCheckService.delete((QrCheck) qrCheckService.get(TestConstant.CHECK_ID).getData());
        accountService.delete(accountService.getAccountByClientId(TestConstant.CLIENT_ID));
        accountService.delete(accountService.getAccountByClientId(TestConstant.SHOP_ID));
        accountService.delete(accountService.getAccountByClientId(TestConstant.BANK_ID));
    }
}
