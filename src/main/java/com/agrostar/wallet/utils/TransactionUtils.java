package com.agrostar.wallet.utils;

public class TransactionUtils {

    public static String getTransactionId() {
        StringBuffer transactionId = new StringBuffer(RandomStringGenerator.getInstance().nextString());
        return transactionId.append(Long.toHexString(System.currentTimeMillis())).toString();
    }

}
