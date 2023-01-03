package com.ndt2101.ezimarket.specification;

import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.ndt2101.ezimarket.specification.SearchOperation.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSpecification implements Specification<UserLoginDataEntity> {

    private SearchCriteria searchCriteria;

    @Override
    public Specification<UserLoginDataEntity> and(Specification<UserLoginDataEntity> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<UserLoginDataEntity> or(Specification<UserLoginDataEntity> other) {
        return Specification.super.or(other);
    }

    @Override
    public Predicate toPredicate(Root<UserLoginDataEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (searchCriteria.getOperation().equals(EQUAL)) {
            return criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } else if (searchCriteria.getOperation().equals(LIKE)) {
            return criteriaBuilder.like(root.get(searchCriteria.getKey()),"%" + searchCriteria.getValue() + "%");
        } else {
            return null;
        }
    }
}
