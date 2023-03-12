package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.paypal.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
