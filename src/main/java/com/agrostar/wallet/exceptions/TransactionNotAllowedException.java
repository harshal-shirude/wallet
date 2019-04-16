package com.agrostar.wallet.exceptions;

import com.agrostar.wallet.enums.TransactionType;

public class TransactionNotAllowedException extends RuntimeException {

    private TransactionType type;
    private double amount;
    private int walletId;

    public TransactionNotAllowedException(TransactionType type, double amount, int walletId) {
        super(String.format("%s transaction not allowed on wallet %d with amount '%.2f'", type, walletId, amount));
        this.type = type;
        this.amount = amount;
        this.walletId = walletId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public int getWalletId() {
        return walletId;
    }
}
