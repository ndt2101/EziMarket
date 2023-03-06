package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.LikeDTO;
import com.ndt2101.ezimarket.dto.PostDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.pagination.Pagination;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.PostService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import com.ndt2101.ezimarket.utils.FileHandle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl extends BasePagination<PostEntity, PostRepository> implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private FileHandle fileHandle;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository repository) {
        super(repository);
    }

    @Override
    public PostDTO create(PostDTO postDTO, MultipartFile multipartFile) throws ApplicationException {
        ShopEntity shopEntity = shopRepository.findById(postDTO.getShop().getId()).orElseThrow(() -> new NotFoundException("Shop not found"));
        ProductEntity productEntity = productRepository.findById(postDTO.getProduct().getId()).orElseThrow(() -> new NotFoundException("Product not found"));
        VoucherEntity voucherEntity = voucherRepository.findById(postDTO.getVoucher().getId()).orElseThrow(() -> new NotFoundException("Voucher not found"));

        PostEntity postEntity = new PostEntity(shopEntity, postDTO.getPostContentText(), null, productEntity, voucherEntity, null, null);
        postEntity = postRepository.saveAndFlush(postEntity);

        try {
            String fileName = multipartFile.getOriginalFilename();                        // to get original file name
            fileName = UUID.randomUUID().toString().concat(fileHandle.getExtension(fileName));  // to generated random string values for file name.
            File file = fileHandle.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
            String path = "images/" + postDTO.getShop().getId() + "/posts/" + postEntity.getId() + "/" + fileName;
            String imageUrl = fileHandle.uploadFile(file, path);                               // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder

            ImageEntity imageEntity = imageRepository.save(new ImageEntity(fileName, imageUrl, null, null));
            postEntity.setImage(imageEntity);
            postRepository.save(postEntity);
            postDTO = mapper.map(postEntity, PostDTO.class);
            postDTO.getVoucher().setImg(shopEntity.getUserLoginData().getAvatarUrl());
            postDTO.getShop().setAvatar(shopEntity.getUserLoginData().getAvatarUrl());
            postDTO.setImageUrl(imageEntity.getUrl());
            postDTO.getProduct().setImages(List.of(productEntity.getImageEntities().get(0).getUrl()));
            return postDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ApplicationException(HttpStatus.UNPROCESSABLE_ENTITY, "Create post unsuccessfully");
    }

    @Override
    public PaginateDTO<PostDTO> getList(Long userId, Long categoryId, Integer page, Integer perPage) {
        Page<PostEntity> pageData = postRepository.findAllByProductCategoryId(categoryId, PageRequest.of(page - 1, perPage, Sort.by("createdTime").descending()));
        return mapPaginate(userId, pageData, page, perPage);
    }

    @Override
    public LikeDTO like(LikeDTO likeDTO) {
        PostEntity postEntity = postRepository.findById(likeDTO.getPostId()).orElseThrow(() -> new NotFoundException("Post not found"));
        UserLoginDataEntity user = userRepository.findById(likeDTO.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        if (!postEntity.getLikes().contains(user)) {
            postEntity.getLikes().add(user);
            postEntity = postRepository.save(postEntity);
        }
        return new LikeDTO(postEntity.getId(), likeDTO.getUserId(), postEntity.getLikes().size(), postEntity.getLikes().contains(user));
    }

    @Override
    public PaginateDTO<PostDTO> getFollowingPost(Long userId, Integer page, Integer perPage) {
        GenericSpecification<FollowerEntity> followerSpecification = new GenericSpecification<FollowerEntity>();
        followerSpecification.add(new SearchCriteria("from", userId, SearchOperation.EQUAL));
        List<Long> shopIds = followerRepository.findAll(followerSpecification).stream().map(followerEntity -> followerEntity.getTo().getId()).toList();

        if (!shopIds.isEmpty()){
            GenericSpecification<PostEntity> specification = new GenericSpecification<>();
            specification.add(new SearchCriteria("shop", shopIds, SearchOperation.IN));

            PaginateDTO<PostEntity> postEntityPaginateDTO = this.paginate(page, perPage, specification);
            return mapPaginate(userId, postEntityPaginateDTO.getPageData(), page, perPage);
        }
        throw new NotFoundException("Post not found");
    }

    private PaginateDTO<PostDTO> mapPaginate(Long userId, Page<PostEntity> pageData, Integer page, Integer perPage) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (perPage == null || perPage <= 0) {
            perPage = Common.PAGING_DEFAULT_LIMIT;
        }
        UserLoginDataEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Pagination pagination = new Pagination(page, perPage, pageData.getTotalPages(), pageData.getTotalElements());
        List<PostDTO> postDTOs = pageData.stream()
                .map(postEntity -> {
                    PostDTO postDTO = mapper.map(postEntity, PostDTO.class);
                    postDTO.getVoucher().setImg(postEntity.getShop().getUserLoginData().getAvatarUrl());
                    postDTO.getShop().setAvatar(postEntity.getShop().getUserLoginData().getAvatarUrl());
                    if ( postEntity.getImage()!=null ){
                        postDTO.setImageUrl(postEntity.getImage().getUrl());
                    }
                    postDTO.getProduct().setImages(List.of(postEntity.getProduct().getImageEntities().get(0).getUrl()));
                    postDTO.setLikes(new LikeDTO(postEntity.getId(), userId, postEntity.getLikes().size(), postEntity.getLikes().contains(user)));
                    return postDTO;
                })
                .toList();
        Page<PostDTO> postDTOPageData = new PageImpl<>(postDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(postDTOPageData, pagination);
    }
}
