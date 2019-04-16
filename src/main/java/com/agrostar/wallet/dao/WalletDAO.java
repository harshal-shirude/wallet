package com.agrostar.wallet.dao;

import com.agrostar.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletDAO extends JpaRepository<Wallet, Integer> {
}
