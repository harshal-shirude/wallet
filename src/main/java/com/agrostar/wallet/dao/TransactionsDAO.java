package com.agrostar.wallet.dao;

import com.agrostar.wallet.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsDAO extends JpaRepository<Transactions, String> {

    @Query(value = "SELECT t FROM Transactions t WHERE t.wallet.id = ?1 AND t.status = 'SUCCESS' ORDER BY t.createdAt DESC")
    List<Transactions> findAllCompletedTransactionsByWallet(int walletId);

    @Query(value = "SELECT t FROM Transactions t WHERE t.id=?1 AND t.wallet.id = ?2")
    Optional<Transactions> findByIdAndWalletId(String id, int walletId);
}
