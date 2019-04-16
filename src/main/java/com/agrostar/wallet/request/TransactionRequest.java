package com.agrostar.wallet.request;

import com.agrostar.wallet.enums.TransactionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class TransactionRequest {

    @NotNull(message = "Amount should not be null")
    @PositiveOrZero(message = "Amount value should be 0 or more")
    private double amount;
    @NotNull
    private TransactionType type;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "amount=" + amount +
                ", type=" + type +
                '}';
    }
}
