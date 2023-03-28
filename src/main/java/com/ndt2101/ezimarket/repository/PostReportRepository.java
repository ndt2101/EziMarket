package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
    Boolean existsByPost_Id(Long postId);
    Optional<PostReportEntity> findByPost_Id(Long postId);
}
