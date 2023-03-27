package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.NewsEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

public interface NewsService {
    NewsEntity create(NewsEntity news);

    PaginateDTO<?> getAllNews(Integer page, Integer perPage, GenericSpecification<NewsEntity> specification);

    NewsEntity getDetail(Long id);

    void deleteNews(Long id);
}
