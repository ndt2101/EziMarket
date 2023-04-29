package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.ImageDTO;
import com.ndt2101.ezimarket.dto.ReviewDTO;
import com.ndt2101.ezimarket.dto.UserDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.model.ReviewEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.ProductRepository;
import com.ndt2101.ezimarket.repository.ReviewRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.ReviewService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReviewServiceImpl extends BasePagination<ReviewEntity, ReviewRepository> implements ReviewService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        super(reviewRepository);
    }
    @Override
    public ReviewDTO createReview(ReviewDTO review) {
        UserLoginDataEntity user = userRepository.findById(review.getUser().getId()).orElseThrow(Common.userNotFound);
        ProductEntity productEntity = productRepository.findById(review.getProduct().getId()).orElseThrow(Common.productTypeNotFound);

        int productReviewNums = productEntity.getReviews().size();
        AtomicInteger productNums = new AtomicInteger(0);
        productEntity.getShop().getProductEntities().forEach(product -> {
            if (product.getReviews().size() > 0) {
                productNums.getAndIncrement();
            }
        });

        Float oldTempShopRate = productEntity.getShop().getRate() * productNums.get() - productEntity.getRate();

        Float newProductRate = (productEntity.getRate() * productReviewNums + review.getRate())/(productReviewNums + 1);
        productEntity.setRate(newProductRate);

        if (productNums.get() == 0) {
            productNums.set(1);
        } else if (productEntity.getReviews().size() == 0) {
            productNums.getAndIncrement();
        }

        Float newShopRate = (oldTempShopRate + productEntity.getRate()) / productNums.get();
        productEntity.getShop().setRate(newShopRate);
        shopRepository.save(productEntity.getShop());

        ReviewEntity reviewEntity = mapper.map(review, ReviewEntity.class);
        reviewEntity.setUser(user);
        reviewEntity.setProduct(productEntity);
        reviewEntity = reviewRepository.save(reviewEntity);
        productRepository.saveAndFlush(productEntity);
        ReviewDTO result = mapper.map(reviewEntity, ReviewDTO.class);
        result.setCreatedTime(reviewEntity.getCreatedTime().getTime());
        result.setProduct(review.getProduct());
        return result;
    }

    @Override
    public PaginateDTO<ReviewDTO> getReviews(Integer page, Integer perPage, GenericSpecification<ReviewEntity> specification) {
        PaginateDTO<ReviewEntity> reviewEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<ReviewDTO> reviewDTOs = reviewEntityPaginateDTO.getPageData().stream().map(reviewEntity -> {
            ReviewDTO result = mapper.map(reviewEntity, ReviewDTO.class);
            result.setCreatedTime(reviewEntity.getCreatedTime().getTime());
            List<ImageDTO> imageDTOs = reviewEntity.getProduct().getImageEntities().stream().map(imageEntity -> mapper.map(imageEntity, ImageDTO.class)).toList();
            result.getProduct().setImages(imageDTOs);
            result.getProduct().getShop().setAvatar(reviewEntity.getProduct().getShop().getUserLoginData().getAvatarUrl());

            UserDTO userDTO = new UserDTO();
            userDTO.setAvatarUrl(reviewEntity.getUser().getAvatarUrl());
            userDTO.setId(reviewEntity.getUser().getId());
            userDTO.setFirstName(reviewEntity.getUser().getFirstName());
            userDTO.setLastName(reviewEntity.getUser().getLastName());

            result.setUser(userDTO);
            return result;
        }).toList();

        Page<ReviewDTO> pageData = new PageImpl<>(reviewDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, reviewEntityPaginateDTO.getPagination());
    }
}
