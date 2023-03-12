package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.paypal.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
