package com.api.bank;


import com.api.bank.manager.IBankManager;
import com.api.bank.model.BankConstants;
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankTransactionTests {
    public static boolean started = false;
    private final static String SUCCESS = "Success";
    @Autowired
    IBankManager bankManager;

    @Autowired
    AccountService accountService;

    @Autowired
    CheckService qrCheckService;


//    Account individualAccount;
//    Account shopAccount;
//    Account bankAccount;

    @Test
    @BeforeAll
    void testInitAccounts() {

        //Arrange
        ObjectResponse res1 = null;
        ObjectResponse res2 = null;
        ObjectResponse res3 = null;


        var individualClient = new Client(TestConst.CLIENT_ID, TestConst.CLIENT_USERNAME, TestConst.CLIENT_USERNAME, SocialReasonStatus.INDIVIDUAL);
        var shopClient = new Client(TestConst.SHOP_ID, TestConst.SHOP_USERNAME, SocialReasonStatus.COMPANY);
        var bankClient = new Client(TestConst.BANK_ID, TestConst.BANK_USERNAME, SocialReasonStatus.BANK);

        var card = new Card(TestConst.CARD_ID);

        Account individualAccount = new Account(100000, individualClient, card);
        Account shopAccount = new Account(0, shopClient);
        Account bankAccount = new Account(10000000, bankClient);

        //Act

        if (!accountService.exist(TestConst.CLIENT_ID.toString())) {
            res1 = accountService.add(individualAccount);
        } else {
            res1 = accountService.get(TestConst.CLIENT_ID.toString());
        }

        if (!accountService.exist(TestConst.SHOP_ID.toString())) {
            res2 = accountService.add(shopAccount);
        } else {
            res2 = accountService.get(TestConst.SHOP_ID.toString());
        }

        if (!accountService.exist(TestConst.BANK_ID.toString())) {
            res3 = accountService.add(bankAccount);
        } else {
            res3 = accountService.get(TestConst.BANK_ID.toString());
        }

        //Assert

        assertNotNull(res1);
        assertEquals(SUCCESS, res1.getMessage());

        assertNotNull(res2);
        assertEquals(SUCCESS, res2.getMessage());

        assertNotNull(res3);
        assertEquals(SUCCESS, res3.getMessage());
    }

    @Test
    void testBuyQrCode() {
        //Arrange
        var originalBankSold = ((Account)accountService.get(BankConstants.BANK_ID).getData()).getSold();
        var originalClientSold = accountService.getAccountByClientId(TestConst.CLIENT_ID).getSold();

        Account bankAccountAfter;
        Account clientAccountAfter;

        var getQrCheck = qrCheckService.getCheckByCheckToken(TestConst.CHECK_ID);

        if (getQrCheck != null) {
            qrCheckService.delete(getQrCheck);
        }

        var transaction = new QrCheckTransactionModel(UUID.randomUUID().toString(), TestConst.CHECK_ID, 100d, TestConst.CLIENT_ID, PaymentMethod.TRANSFER);

        //Act
        var res = bankManager.buyCheckTransaction(transaction);
        bankAccountAfter = ((Account)accountService.get(BankConstants.BANK_ID).getData());
        clientAccountAfter = accountService.getAccountByClientId(TestConst.CLIENT_ID);
        var check = qrCheckService.getCheckByCheckToken(TestConst.CHECK_ID);

        //Assert
        assertNotNull(res);
        assertEquals(TransactionStatus.SUCCESS, res.getTransactionStatus());
        assertEquals(originalBankSold, bankAccountAfter.getSold() - 100d);
        assertEquals(originalClientSold, clientAccountAfter.getSold() + 100d);

        assertNotNull(check);
        assertEquals(TestConst.CHECK_ID, check.getCheckToken().toString());
        assertEquals(100d, check.getSoldAmount());
        assertEquals(365d, check.getNbDayOfValidity());
    }

    @Test
    void shoppingTransactionByCard() {

        //Arrange
        double originalDepositSold = accountService.getAccountByClientId(TestConst.SHOP_ID).getSold();
        double originalWithdrawSold = accountService.getAccountByClientId(TestConst.CLIENT_ID).getSold();

        double actualDepositAccount;
        double actualWithdrawAccount;

        ShoppingTransactionModel transaction = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME, "test", TestConst.CARD_ID, 10, PaymentMethod.CARD);

        //Act
        var res = bankManager.shoppingTransaction(transaction);
        actualDepositAccount = accountService.getAccountByClientId(TestConst.SHOP_ID).getSold();
        actualWithdrawAccount = accountService.getAccountByClientId(TestConst.CLIENT_ID).getSold();

        //Assert
        assertEquals(res.getTransactionStatus(), TransactionStatus.SUCCESS);
        assertEquals(originalDepositSold + 10.0d, actualDepositAccount);
        assertEquals(originalWithdrawSold - 10.0d, actualWithdrawAccount);
    }

    @Test
    void shoppingTransactionByQrCheck() {

        //Arrange
        double originalDepositSold = accountService.getAccountByClientId(TestConst.SHOP_ID).getSold();
        double originalWithdrawSold = accountService.getAccountByOwnerName(BankConstants.BANK_NAME).getSold();

        double actualDepositSold;
        double actualWithdrawSold;
        double actualQrCheckSold;


        var checkId = UUID.randomUUID();
        QrCheck qrCheck = (QrCheck) qrCheckService.add(new QrCheck(10d, checkId.toString())).getData();
        qrCheckService.persist();
        double qrSold = qrCheck.getSoldAmount();

        ShoppingTransactionModel transaction = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME.toString(), "test", qrCheck.getCheckToken(), 10, PaymentMethod.CHECK);

        //Act
        var res = bankManager.shoppingTransaction(transaction);
        actualDepositSold = accountService.getAccountByClientId(TestConst.SHOP_ID).getSold();
        actualWithdrawSold = accountService.getAccountByOwnerName(BankConstants.BANK_NAME).getSold();
        actualQrCheckSold = qrCheckService.getCheckByCheckToken(checkId.toString()).getSoldAmount();

        //Assert
        assertEquals(res.getTransactionStatus(), TransactionStatus.SUCCESS);
        assertEquals(0, actualQrCheckSold );
        assertEquals((originalDepositSold + 10d), actualDepositSold);
        assertEquals((originalWithdrawSold - 10d), actualWithdrawSold);
    }

    @Test
    @AfterAll
    void shoppingCardTransactionFailed() {
        //Arrange

        double originalDepositSold = accountService.getAccountByClientId(TestConst.SHOP_ID).getSold();
        double originalWithdrawSold = accountService.getAccountByClientId(TestConst.CLIENT_ID).getSold();

        Account individualAccount = accountService.getAccountByClientId(TestConst.CLIENT_ID);

        Account afterWithdrawNotEnoughMoney = null;
        Account afterDepositNotEnoughMoney = null;
        Account afterDepositAccountNullWithdrawAccount = null;
        Account afterDepositAccountNullDepositAccount = null;

        ShoppingTransactionModel transactionCardNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME, "test", "", 10, PaymentMethod.CARD);
        ShoppingTransactionModel transactionCardExpired = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME, "test", TestConst.CARD_ID, 10, PaymentMethod.CARD);

        ShoppingTransactionModel transactionWithdrawalAccountNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME, "test", "notAValidCardId", 10, PaymentMethod.CARD);
        ShoppingTransactionModel transactionNotEnoughMoney = new ShoppingTransactionModel(UUID.randomUUID().toString(), TestConst.SHOP_USERNAME, "test", TestConst.CARD_ID, 1000000000, PaymentMethod.CARD);

        ShoppingTransactionModel transactionDepositAccountNull = new ShoppingTransactionModel(UUID.randomUUID().toString(), "dede", "test", TestConst.CARD_ID, 10, PaymentMethod.CARD);

        //Act
        var resTransactionCardNull = bankManager.shoppingTransaction(transactionCardNull);

        individualAccount.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2020-02-13T18:51:09.840Z"))));
        accountService.update(individualAccount);
        var resTransactionCardExpired = bankManager.shoppingTransaction(transactionCardExpired);
        individualAccount.getCard().setExpirationDate(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2050-02-13T18:51:09.840Z"))));
        accountService.update(individualAccount);

        var resTransactionWithdrawalAccountNull = bankManager.shoppingTransaction(transactionWithdrawalAccountNull);

        var resTransactionNotEnoughMoney = bankManager.shoppingTransaction(transactionNotEnoughMoney);
        afterWithdrawNotEnoughMoney = accountService.getAccountByClientId(TestConst.CLIENT_ID);
        afterDepositNotEnoughMoney = accountService.getAccountByClientId(TestConst.SHOP_ID);

        var resTransactionDepositAccountNull = bankManager.shoppingTransaction(transactionDepositAccountNull);
        afterDepositAccountNullWithdrawAccount = accountService.getAccountByClientId(TestConst.CLIENT_ID);
        afterDepositAccountNullDepositAccount = accountService.getAccountByClientId(TestConst.SHOP_ID);

        //Assert

        assertEquals(resTransactionCardNull.getTransactionStatus(), TransactionStatus.CARD_ERROR);

        assertEquals(resTransactionCardExpired.getTransactionStatus(), TransactionStatus.VALIDITY_DATE_ERROR);

        assertThat(resTransactionWithdrawalAccountNull.getTransactionStatus(), Matchers.either(Matchers.is(TransactionStatus.ACCOUNT_ERROR)).or(Matchers.is(TransactionStatus.BANK_ERROR)));

        assertEquals(resTransactionNotEnoughMoney.getTransactionStatus(), TransactionStatus.INSUFFICIENT_FUNDS_ERROR);
        assertEquals(afterWithdrawNotEnoughMoney.getSold(), originalWithdrawSold);
        assertEquals(afterDepositNotEnoughMoney.getSold(), originalDepositSold);

        assertThat(resTransactionDepositAccountNull.getTransactionStatus(), Matchers.either(Matchers.is(TransactionStatus.ACCOUNT_ERROR)).or(Matchers.is(TransactionStatus.BANK_ERROR)));
        assertEquals(afterDepositAccountNullWithdrawAccount.getSold(), originalWithdrawSold);
        assertEquals(afterDepositAccountNullDepositAccount.getSold(), originalDepositSold);

    }

//    @Override
//    public void beforeAll(ExtensionContext context) throws Exception {
//        if (!started) {
//            started = true;
//            testInitAccounts();
//        }
//    }

    //    @Override
    public void close() throws Throwable {
        started = false;
//        deleteAll();

    }

    private void deleteAll() {

        qrCheckService.delete((QrCheck) qrCheckService.get(TestConst.CHECK_ID).getData());
        accountService.delete(accountService.getAccountByClientId(TestConst.CLIENT_ID));
        accountService.delete(accountService.getAccountByClientId(TestConst.SHOP_ID));
        accountService.delete(accountService.getAccountByClientId(TestConst.BANK_ID));
    }
}
