package com.agrostar.wallet.service;

import com.agrostar.wallet.dao.TransactionsDAO;
import com.agrostar.wallet.dao.WalletDAO;
import com.agrostar.wallet.entity.Transactions;
import com.agrostar.wallet.entity.Wallet;
import com.agrostar.wallet.enums.TransactionType;
import com.agrostar.wallet.enums.WalletStatus;
import com.agrostar.wallet.exceptions.TransactionNotAllowedException;
import com.agrostar.wallet.request.TransactionRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    private TransactionsDAO transactionsDAO;

    @Mock
    private WalletDAO walletDAO;

    @InjectMocks
    private TransactionService transactionService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(transactionService,"maxAllowedBalance", 1000.00);
        ReflectionTestUtils.setField(transactionService,"minAllowedBalance", -1000.00);
    }

    private Wallet getWallet(Double balance) {
        Wallet wallet = new Wallet();
        wallet.setId(1);
        wallet.setBalance(balance);
        wallet.setStatus(WalletStatus.ACTIVATED);
        return wallet;
    }

    @Test
    public void teat_createCreditTransactionOnExistingWallet() {
        Wallet wallet = getWallet(100.00);
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100);
        request.setType(TransactionType.CREDIT);

        Transactions transaction = new Transactions(request.getType(), request.getAmount(), "", wallet);

        Mockito.when(walletDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(wallet));
        Mockito.when(transactionsDAO.save(ArgumentMatchers.any(Transactions.class))).thenReturn(transaction);
        Transactions expected = transactionService.createTransaction(wallet.getId(), request);
        Assert.assertEquals(200.00, expected.getWallet().getBalance(), 0);
    }

    @Test
    public void teat_createDebitTransactionOnExistingWallet() {
        Wallet wallet = getWallet(100.00);

        TransactionRequest request = new TransactionRequest();
        request.setAmount(100);
        request.setType(TransactionType.DEBIT);

        Transactions transaction = new Transactions(request.getType(), request.getAmount(), "", wallet);

        Mockito.when(walletDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(wallet));
        Mockito.when(transactionsDAO.save(ArgumentMatchers.any(Transactions.class))).thenReturn(transaction);
        Transactions expected = transactionService.createTransaction(wallet.getId(), request);
        Assert.assertEquals(0, expected.getWallet().getBalance(), 0);
    }

    @Test(expected = TransactionNotAllowedException.class)
    public void teat_createCreditTransactionWithMaxAlloweBalance() {
        Wallet wallet = getWallet(100.00);
        TransactionRequest request = new TransactionRequest();
        request.setAmount(1000);
        request.setType(TransactionType.CREDIT);

        Transactions transaction = new Transactions(request.getType(), request.getAmount(), "", wallet);

        Mockito.when(walletDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(wallet));
        Mockito.when(transactionsDAO.save(ArgumentMatchers.any(Transactions.class))).thenReturn(transaction);
        transactionService.createTransaction(wallet.getId(), request);
    }

    @Test(expected = TransactionNotAllowedException.class)
    public void teat_createDebitTransactionWithMinAlloweBalance() {
        Wallet wallet = getWallet(100.00);
        TransactionRequest request = new TransactionRequest();
        request.setAmount(1000);
        request.setType(TransactionType.DEBIT);

        Transactions transaction = new Transactions(request.getType(), request.getAmount(), "", wallet);

        Mockito.when(walletDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(wallet));
        Mockito.when(transactionsDAO.save(ArgumentMatchers.any(Transactions.class))).thenReturn(transaction);
        transactionService.createTransaction(wallet.getId(), request);
    }

    @Test
    public void test_cancelTransaction() {
        Wallet wallet = getWallet(100.00);
        Transactions transaction = new Transactions(TransactionType.CREDIT, 50, "", wallet);
        Mockito.when(walletDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(wallet));
        Mockito.when(transactionsDAO.findByIdAndWalletId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.of(transaction));
        Mockito.when(transactionsDAO.save(ArgumentMatchers.any(Transactions.class))).thenReturn(transaction);
        transactionService.cancelTransaction(transaction.getId(), wallet.getId());
        Assert.assertEquals(wallet.getBalance(),50, 0);
    }

}
