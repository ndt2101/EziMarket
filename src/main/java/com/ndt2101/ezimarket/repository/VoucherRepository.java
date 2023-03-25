package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VoucherRepository extends JpaRepository<VoucherEntity, Long>, JpaSpecificationExecutor<VoucherEntity> {

    @Transactional
    @Query(value = "delete from user_voucher uv where uv.user_id = ?1 and uv.voucher_id = ?2", nativeQuery = true)
    void deleteUserVoucher(Long userId, Long voucherId);
}
