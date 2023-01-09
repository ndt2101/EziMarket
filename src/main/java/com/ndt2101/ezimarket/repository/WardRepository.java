package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ProvinceEntity;
import com.ndt2101.ezimarket.model.WardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardRepository extends JpaRepository<WardEntity, String> {

}

