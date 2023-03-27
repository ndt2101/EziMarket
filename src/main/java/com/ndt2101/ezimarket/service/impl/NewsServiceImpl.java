package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.NewsEntity;
import com.ndt2101.ezimarket.repository.NewsRepository;
import com.ndt2101.ezimarket.service.NewsService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends BasePagination<NewsEntity, NewsRepository> implements NewsService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    public NewsServiceImpl(NewsRepository repository) {
        super(repository);
    }
    @Override
    public NewsEntity create(NewsEntity news) {
        return newsRepository.save(news);
    }

    @Override
    public PaginateDTO<NewsEntity> getAllNews(Integer page, Integer perPage, GenericSpecification<NewsEntity> specification) {
        PaginateDTO<NewsEntity> paginateDTO = paginate(page, perPage, specification);
        paginateDTO.getPageData().forEach(news -> {
            news.setContent(null);
        });
        return paginateDTO;
    }

    @Override
    public NewsEntity getDetail(Long id) {
        return newsRepository.findById(id).orElseThrow(() ->new NotFoundException("News not found"));
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }


}
