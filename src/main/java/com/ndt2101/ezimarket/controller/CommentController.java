package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.CommentDTO;
import com.ndt2101.ezimarket.model.CommentEntity;
import com.ndt2101.ezimarket.service.CommentService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;

@RestController
@RequestMapping("/api/comment")
public class CommentController extends BaseController<Object>{
    @Autowired
    private CommentService commentService;

    @PostMapping("/post")
    public ResponseEntity<?> createPostComment(@RequestBody CommentDTO commentDTO) {
        return successfulResponse(commentService.createPostComment(commentDTO));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPostComment(
            @PathVariable(name = "id") Long postId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        GenericSpecification<CommentEntity> specification = new GenericSpecification<CommentEntity>();
        specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "post", "id", postId, JoinType.INNER));
        specification.add(new SearchCriteria("parent", null, SearchOperation.NULL));
        return resPagination(commentService.getPostComments(specification, page, perPage));
    }
}
