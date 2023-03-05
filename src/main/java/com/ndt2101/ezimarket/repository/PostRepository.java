package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.CategoryEntity;
import com.ndt2101.ezimarket.model.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    @Query("select ps from PostEntity ps where ps.product in (select p from ProductEntity p where p.category = (select c from CategoryEntity c where c.id = :categoryId))")
    Page<PostEntity> findAllByProductCategoryId(Long categoryId, Pageable pageable);
}
