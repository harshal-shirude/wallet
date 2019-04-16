package com.agrostar.wallet.service;

import com.agrostar.wallet.dao.TransactionsDAO;
import com.agrostar.wallet.dao.WalletDAO;
import com.agrostar.wallet.entity.Transactions;
import com.agrostar.wallet.entity.Wallet;
import com.agrostar.wallet.enums.TransactionStatus;
import com.agrostar.wallet.enums.WalletStatus;
import com.agrostar.wallet.exceptions.NoResourceFoundException;
import com.agrostar.wallet.exceptions.OperationNotAllowedException;
import com.agrostar.wallet.exceptions.TransactionNotAllowedException;
import com.agrostar.wallet.request.TransactionRequest;
import com.agrostar.wallet.response.PassbookResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionsDAO transactionsDAO;

    @Autowired
    private WalletDAO walletDAO;

    @Value("${wallet.min_balance}")
    private Double minAllowedBalance;
    @Value("${wallet.max_balance}")
    private Double maxAllowedBalance;

    @Transactional
    public Transactions createTransaction(Integer  walletId, TransactionRequest transactionRequest) {
        Wallet wallet = walletDAO.findById(walletId)
                .orElseThrow(() -> new NoResourceFoundException("wallet", "id", walletId));
        Transactions transaction = new Transactions(transactionRequest.getType(), transactionRequest.getAmount(), "", wallet);
        switch (transaction.getType()) {
            case DEBIT:
                wallet.setBalance(wallet.getBalance() - transactionRequest.getAmount());
                if (wallet.getBalance() < minAllowedBalance) {
                    throw new TransactionNotAllowedException(transaction.getType(), transaction.getAmount(), walletId);
                }
                break;
            case CREDIT:
                wallet.setBalance(wallet.getBalance() + transactionRequest.getAmount());
                if (wallet.getBalance() > maxAllowedBalance) {
                    throw new TransactionNotAllowedException(transaction.getType(), transaction.getAmount(), walletId);
                }
                break;

        }
        return transactionsDAO.save(transaction);
    }

    @Transactional
    public boolean cancelTransaction(String transactionId, int walletId) {
        Wallet wallet = walletDAO.findById(walletId)
                .orElseThrow(() -> new NoResourceFoundException("wallet", "id", walletId));
        Transactions transaction = transactionsDAO.findByIdAndWalletId(transactionId, walletId)
                .orElseThrow(() -> new NoResourceFoundException("transaction", "id", transactionId));
        if (transaction.getStatus() == TransactionStatus.CANCELLED || wallet.getStatus() != WalletStatus.ACTIVATED) {
            throw new OperationNotAllowedException("CANCEL", "Transaction", "Either transaction is already cancelled or Wallet is deactivated", transaction.getId());
        }
        Wallet associatedWallet = transaction.getWallet();
        switch (transaction.getType()) {
            case DEBIT:
                associatedWallet.setBalance(associatedWallet.getBalance() + transaction.getAmount());
                if (associatedWallet.getBalance() > maxAllowedBalance) {
                    throw new OperationNotAllowedException("CANCEL", "Transaction", "New balance exceeds Maximum allowed balance", transaction.getId());
                }
                break;
            case CREDIT:
                associatedWallet.setBalance(associatedWallet.getBalance() - transaction.getAmount());
                if (associatedWallet.getBalance() < minAllowedBalance) {
                    throw new OperationNotAllowedException("CANCEL", "Transaction", "New balance is less than Minimum allowed balance", transactionId);
                }
                break;

        }
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionsDAO.save(transaction);
        return true;
    }

    public List<PassbookResponseData> getAllTransactionByWallet(int walletId) {
        Wallet wallet = walletDAO.findById(walletId)
                .orElseThrow(() -> new NoResourceFoundException("wallet", "id", walletId));
        if(wallet.getStatus() == WalletStatus.DEACTIVATED) {
            throw new OperationNotAllowedException("GET_TRANSACTIONS", "Wallet", "Wallet is deactivated", walletId);
        }
        List<Transactions> transactions = transactionsDAO.findAllCompletedTransactionsByWallet(walletId);
        List<PassbookResponseData> passbookResponses = new ArrayList<>();
        for(Transactions transaction : transactions) {
            PassbookResponseData passbookResponse = new PassbookResponseData(transaction.getType(), transaction.getAmount(), transaction.getSummary());
            passbookResponses.add(passbookResponse);
        }
        return passbookResponses;
    }

}
