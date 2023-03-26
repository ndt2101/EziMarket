package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.ReviewDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.ReviewEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);

    PaginateDTO<ReviewDTO> getReviews(Integer page, Integer perPage, GenericSpecification<ReviewEntity> specification);
}
