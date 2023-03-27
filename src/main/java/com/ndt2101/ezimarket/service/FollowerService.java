package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.model.FollowerEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

import java.util.Map;

public interface FollowerService {
    Boolean checkFollowed(GenericSpecification<FollowerEntity> specification);

    Boolean follow(Long shopId, Long userId);

    Map<String, String> getFollowNums(Long id, String type);
}
