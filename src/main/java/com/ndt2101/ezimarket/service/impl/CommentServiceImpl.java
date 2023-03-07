package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.dto.CommentDTO;
import com.ndt2101.ezimarket.dto.PostDTO;
import com.ndt2101.ezimarket.dto.UserDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.CommentEntity;
import com.ndt2101.ezimarket.model.PostEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.CommentRepository;
import com.ndt2101.ezimarket.repository.PostRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.CommentService;

import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl extends BasePagination<CommentEntity, CommentRepository> implements CommentService {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        super(commentRepository);
    }


    @Override
    public CommentDTO createPostComment(CommentDTO commentDTO) {
        UserLoginDataEntity userEntity = userRepository.findById(commentDTO.getUser().getId()).orElseThrow(() -> new NotFoundException("User not found"));
        PostEntity postEntity = postRepository.findById(commentDTO.getPostId()).orElseThrow(() -> new NotFoundException("Post not found"));
        CommentEntity commentEntity = mapper.map(commentDTO, CommentEntity.class);
        CommentEntity parentEntity;
        if (commentDTO.getParentId() != null) {
            parentEntity = commentRepository.findById(commentDTO.getParentId()).orElseThrow(() -> new NotFoundException("Parent comment not found"));
            commentEntity.setParent(parentEntity);
        }
        commentEntity.setPost(postEntity);
        commentEntity.setUser(userEntity);
        commentEntity = commentRepository.save(commentEntity);
        commentDTO = mapParentAndChildrenDTO(commentEntity);
        return commentDTO;
    }

    private CommentDTO mapParentAndChildrenDTO(CommentEntity commentEntity) {
        CommentDTO commentDTO = mapCommentDTO(commentEntity);

        List<CommentDTO> childrenDTOs = new ArrayList<>();
        if (commentEntity.getChildren() != null) {
            commentEntity.getChildren().forEach(children -> {
                CommentDTO childrenDTO = mapCommentDTO(children);
                childrenDTOs.add(childrenDTO);
            });
            commentDTO.setChildren(childrenDTOs);
        }
        return commentDTO;
    }

    private CommentDTO setUserDTO(CommentDTO commentDTO, CommentEntity commentEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setAvatarUrl(commentEntity.getUser().getAvatarUrl());
        userDTO.setId(commentEntity.getUser().getId());
        userDTO.setFirstName(commentEntity.getUser().getFirstName());
        userDTO.setLastName(commentEntity.getUser().getLastName());
        commentDTO.setUser(userDTO);
        return commentDTO;
    }

    private CommentDTO mapCommentDTO(CommentEntity commentEntity) {

        CommentDTO commentDTO = mapper.map(commentEntity, CommentDTO.class);

        setUserDTO(commentDTO, commentEntity);
        if (commentEntity.getParent() != null) {
            commentDTO.setParentId(commentEntity.getParent().getId());
        }
        commentDTO.setPostId(commentEntity.getPost().getId());
        commentDTO.setCreateTime(commentEntity.getCreatedTime());
        return commentDTO;
    }

    @Override
    public PaginateDTO<CommentDTO> getPostComments(GenericSpecification<CommentEntity> specification, Integer page, Integer perPage) {
        PaginateDTO<CommentEntity> commentEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<CommentDTO> commentDTOs = commentEntityPaginateDTO.getPageData().stream().map(this::mapParentAndChildrenDTO).toList();
        Page<CommentDTO> commentDTOPage = new PageImpl<>(commentDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(commentDTOPage, commentEntityPaginateDTO.getPagination());
    }
}
