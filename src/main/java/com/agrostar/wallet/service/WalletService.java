package com.agrostar.wallet.service;

import com.agrostar.wallet.dao.WalletDAO;
import com.agrostar.wallet.entity.Wallet;
import com.agrostar.wallet.enums.WalletStatus;
import com.agrostar.wallet.exceptions.NoResourceFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletDAO walletDAO;

    public Double getWalletBalance(int walletId) {
        Wallet wallet = walletDAO.findById(walletId)
                .orElseThrow(() -> new NoResourceFoundException("wallet", "id", walletId));
        if(wallet.getStatus() == WalletStatus.DEACTIVATED) {
            throw new NoResourceFoundException("wallet", "id", walletId);
        }
        return wallet.getBalance();
    }

}
