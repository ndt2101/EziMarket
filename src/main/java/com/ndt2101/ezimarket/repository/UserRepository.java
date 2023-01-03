package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.UserAccountEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserLoginDataEntity, Long>, JpaSpecificationExecutor<UserLoginDataEntity> {
    Boolean existsByLoginName(String loginName);
    @NonNull
    Optional<UserLoginDataEntity> findById(@NonNull Long id);
    Boolean existsByEmailAddress(String email);
    Optional<UserLoginDataEntity> findByLoginName(String loginName);

    Optional<UserLoginDataEntity> findByEmailAddress(String email);
    @NonNull
    List<UserLoginDataEntity> findAll(Specification<UserLoginDataEntity> specification);
}
