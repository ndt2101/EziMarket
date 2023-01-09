package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.DistrictEntity;
import com.ndt2101.ezimarket.model.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<DistrictEntity, Long> {

}
