package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.paypal.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

}
