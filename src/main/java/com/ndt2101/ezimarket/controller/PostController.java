package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.LikeDTO;
import com.ndt2101.ezimarket.dto.PostDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.repository.CategoryRepository;
import com.ndt2101.ezimarket.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
public class PostController extends BaseController<Object> {
    @Autowired
    private PostService postService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@ModelAttribute(name = "postDTO") PostDTO postDTO, @ModelAttribute MultipartFile file) throws ApplicationException {
        return this.successfulResponse(postService.create(postDTO, file));
    }

    @GetMapping
    public ResponseEntity<?> getList(
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        return resPagination(postService.getList(userId, categoryId, page, perPage));
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
}
