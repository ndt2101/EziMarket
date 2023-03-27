package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.FollowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FollowerRepository extends JpaRepository<FollowerEntity, Long>, JpaSpecificationExecutor<FollowerEntity> {
    Long countByTo_Id(Long id); // follower
    Long countByFrom_Id(Long id); // following
}
