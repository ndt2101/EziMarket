package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.model.FollowerEntity;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.repository.FollowerRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.FollowerService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.JoinType;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FollowerServiceImpl extends BasePagination<FollowerEntity, FollowerRepository> implements FollowerService {

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    public FollowerServiceImpl(FollowerRepository repository) {
        super(repository);
    }

    @Override
    public Boolean checkFollowed(GenericSpecification<FollowerEntity> specification) {
        return followerRepository.findOne(specification).isPresent();
    }

    @Override
    public Boolean follow(Long shopId, Long userId) {
        FollowerEntity followerEntity = null;
        GenericSpecification<FollowerEntity> specification = new GenericSpecification<FollowerEntity>();
        if (userId != null && shopId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "to", "id", shopId, JoinType.INNER));
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "from", "id", userId, JoinType.INNER));

            if (followerRepository.findOne(specification).isEmpty()) {
                followerEntity = new FollowerEntity(null, userRepository.findById(userId).orElseThrow(Common.userNotFound), shopRepository.findById(shopId).orElseThrow(Common.shopNotFound), new Date(System.currentTimeMillis()));
                followerEntity = followerRepository.save(followerEntity);
            }
        }
        return followerEntity != null && followerEntity.getId() != null;
    }

    @Override
    public Map<String, String> getFollowNums(Long id, String type) {
        Map<String, String> result = new HashMap<>();
        if (type.equals("shop")) { // la shopId

            ShopEntity shopEntity = shopRepository.findById(id).orElseThrow(Common.shopNotFound);
            GenericSpecification<FollowerEntity> followingSpecification = new GenericSpecification<FollowerEntity>();
            followingSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "from", "id", shopEntity.getUserLoginData().getId(), JoinType.INNER));
            Long following = followerRepository.countByFrom_Id(shopEntity.getUserLoginData().getId());
            result.put("following", String.valueOf(following));

            GenericSpecification<FollowerEntity> followersSpecification = new GenericSpecification<FollowerEntity>();
            followersSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "to", "id", id, JoinType.INNER));
            Long followers = followerRepository.count(followersSpecification);
            result.put("followers", String.valueOf(followers));
        }


        if (type.equals("user")) {
            GenericSpecification<FollowerEntity> followingSpecification = new GenericSpecification<FollowerEntity>();
            followingSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "from", "id", id, JoinType.INNER));
            Long followers = followerRepository.count(followingSpecification);
            result.put("following", String.valueOf(followers));

            result.put("followers", String.valueOf(0L));
        }

        return result;
    }


}
