package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.CommentDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.CommentEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

import java.util.List;

public interface CommentService {
    CommentDTO createPostComment(CommentDTO commentDTO);
    PaginateDTO<CommentDTO> getPostComments(GenericSpecification<CommentEntity> specification, Integer page, Integer perPage);
}
