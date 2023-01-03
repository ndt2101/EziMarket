package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.EmailValidationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface EmailValidationStatusRepository extends JpaRepository<EmailValidationStatusEntity, Long> {
    @NonNull
    Optional<EmailValidationStatusEntity> findById(@NonNull Long id);

    Optional<EmailValidationStatusEntity> findByStatusDescription(String description);
}
