package com.ndt2101.ezimarket.specification;

import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSpecificationBuilder {
    private List<SearchCriteria> params = new ArrayList<>();

    public UserSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }
}
