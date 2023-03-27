package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.model.NewsEntity;
import com.ndt2101.ezimarket.service.NewsService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/news/")
public class NewsController extends BaseController<Object> {

    @Autowired
    private NewsService newsService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewsEntity news) {
        return successfulResponse(newsService.create(news));
    }

    @GetMapping
    public ResponseEntity<?> getAllNews(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request
    ) {
        GenericSpecification<NewsEntity> specification = new GenericSpecification<NewsEntity>().getBasicQuery(request);
        return resPagination(newsService.getAllNews(page, perPage, specification));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getNews(
            @PathVariable("id") Long id
    ) {
        return successfulResponse(newsService.getDetail(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteNews(
            @PathVariable("id") Long id
    ) {
        newsService.deleteNews(id);
        return successfulResponse("Delete successfully");
    }
}
