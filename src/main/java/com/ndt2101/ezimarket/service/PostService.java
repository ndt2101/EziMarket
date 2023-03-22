package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.LikeDTO;
import com.ndt2101.ezimarket.dto.PostDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.PostEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostDTO create(PostDTO postDTO, MultipartFile multipartFile) throws ApplicationException;
    PaginateDTO<PostDTO> getList(Long userId, Long categoryId, Integer page, Integer perPage);
    LikeDTO like(LikeDTO likeDTO);
    PaginateDTO<PostDTO> getFollowingPost(Long id, Integer page, Integer perPage);

    PaginateDTO<?> getPosts(Long userId, GenericSpecification<PostEntity> specification, Integer page, Integer perPage);
}
