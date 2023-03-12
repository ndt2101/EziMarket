package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.paypal.Payer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayerRepository extends JpaRepository<Payer, String> {
    Optional<Payer> findByUserLoginDataEntity_Id(Long userId);
}
