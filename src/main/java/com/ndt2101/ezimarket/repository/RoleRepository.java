package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> getByDescription(String description);
}
