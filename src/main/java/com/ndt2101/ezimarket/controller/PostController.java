package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.LikeDTO;
import com.ndt2101.ezimarket.dto.PostDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.PostEntity;
import com.ndt2101.ezimarket.repository.CategoryRepository;
import com.ndt2101.ezimarket.service.PostService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/post")
public class PostController extends BaseController<Object> {
    @Autowired
    private PostService postService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestPart(name = "post") PostDTO postDTO, @RequestParam MultipartFile image) throws ApplicationException {
        return this.successfulResponse(postService.create(postDTO, image));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getViaCategoryList(
            @PathVariable(name = "category") Long categoryId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        return resPagination(postService.getList(userId, categoryId, page, perPage));
    }

    @GetMapping("/")
    public ResponseEntity<?> getShopPosts( // getAll thi khong truyen vao shopId
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request
    ) {
        GenericSpecification<PostEntity> specification = new  GenericSpecification<PostEntity>().getBasicQuery(request);
        if (shopId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "shop", "id", shopId, JoinType.INNER));
        }
        return resPagination(postService.getPosts(userId, specification, page, perPage));
    }

    @GetMapping("/followed/{id}")
    public ResponseEntity<?> getFollowedPost(
            @PathVariable(name = "id") Long userId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        return this.resPagination(postService.getFollowingPost(userId, page, perPage));
    }

    @PutMapping("/like")
    public ResponseEntity<?> like(@RequestBody LikeDTO likeDTO) {
        return this.successfulResponse(postService.like(likeDTO));
    }

    @PostMapping("report/{id}")
    ResponseEntity<?> report(@PathVariable("id") Long postId) {
        return successfulResponse(postService.report(postId));
    }

    @GetMapping("report")
    ResponseEntity<?> getAllReportedPost() {
        return successfulResponse(postService.getReportedPosts());
    }

    @PutMapping("report/{id}/{status}")
    ResponseEntity<?> handleReport(@PathVariable("id") Long postId, @PathVariable("status") String status) {
        return successfulResponse(postService.handleReport(postId, status));
    }
}
