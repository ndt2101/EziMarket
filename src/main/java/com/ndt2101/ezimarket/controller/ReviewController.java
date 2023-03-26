package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.ReviewDTO;
import com.ndt2101.ezimarket.model.ReviewEntity;
import com.ndt2101.ezimarket.service.ReviewService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/review")
public class ReviewController extends BaseController<Object> {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/")
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO reviewDTO) {
        return successfulResponse(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/")
    public ResponseEntity<?> getReviews(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "userId", required = false) Long userId,
            HttpServletRequest request) {
        GenericSpecification<ReviewEntity> specification = new GenericSpecification<ReviewEntity>().getBasicQuery(request);
        if (productId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "product", "id", productId, JoinType.INNER));
        }
        if (userId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "user", "id", userId, JoinType.INNER));
        }
        return resPagination(reviewService.getReviews(page, perPage, specification));
    }
}
