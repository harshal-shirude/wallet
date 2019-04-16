package com.agrostar.wallet.response;

import com.agrostar.wallet.enums.TransactionType;

public class PassbookResponseData {

    private TransactionType type;
    private double amount;
    private String summary;

    public PassbookResponseData(TransactionType type, double amount, String summary) {
        this.type = type;
        this.amount = amount;
        this.summary = summary;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
