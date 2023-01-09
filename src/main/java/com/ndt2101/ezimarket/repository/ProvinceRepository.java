package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Long> {
}
