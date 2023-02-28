package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.SaleProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleProgramRepository extends JpaRepository<SaleProgramEntity, Long> {
    void deleteAllByEndTimeLessThan(Long currentTime);
    List<SaleProgramEntity> getSaleProgramEntitiesByEndTimeLessThan(Long current);
}
