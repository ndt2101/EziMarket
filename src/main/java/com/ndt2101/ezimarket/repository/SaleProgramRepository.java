package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.SaleProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SaleProgramRepository extends JpaRepository<SaleProgramEntity, Long>, JpaSpecificationExecutor<SaleProgramEntity> {
    void deleteAllByEndTimeLessThan(Long currentTime);
    List<SaleProgramEntity> getSaleProgramEntitiesByEndTimeLessThan(Long current);
}
