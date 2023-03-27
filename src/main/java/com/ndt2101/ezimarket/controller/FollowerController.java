package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.model.FollowerEntity;
import com.ndt2101.ezimarket.service.FollowerService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/follow/")
public class FollowerController extends BaseController<Object> {
    @Autowired
    private FollowerService followerService;

    @GetMapping
    public ResponseEntity<?> checkFollowed(
            @RequestParam(name = "shop", required = false) Long shopId,
            @RequestParam(name = "user", required = false) Long userId,
            HttpServletRequest request) {
        GenericSpecification<FollowerEntity> specification = new GenericSpecification<FollowerEntity>().getBasicQuery(request);
        if (userId != null && shopId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "to", "id", shopId, JoinType.INNER));
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "from", "id", userId, JoinType.INNER));
        }
        return successfulResponse(followerService.checkFollowed(specification));
    }

    @PutMapping("{shopId}/{userId}")
    public ResponseEntity<?> follow(@PathVariable("shopId") Long shopId, @PathVariable("userId") Long userId) {
        return successfulResponse(followerService.follow(shopId, userId));
    }

    @GetMapping("count")
    public ResponseEntity<?> getFollowNums(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "id", required = false) Long id
    ) {
        return successfulResponse(followerService.getFollowNums(id, type));
    }
}
