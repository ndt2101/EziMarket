package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;
import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.dto.UserDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.OrderService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends BasePagination<OrderEntity, OrderRepository> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository repository) {
        super(repository);
    }


    @Override
    public OrderDTO addToCart(OrderItemDTO orderItemDTO) {
        ProductTypeEntity productTypeEntity = productTypeRepository.findById(orderItemDTO.getProductTypeId()).orElseThrow(Common.productTypeNotFound);
        UserLoginDataEntity userEntity = userRepository.findById(orderItemDTO.getUserId()).orElseThrow(Common.userNotFound);

        GenericSpecification<OrderEntity> orderSpecification = new GenericSpecification<OrderEntity>();
        orderSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "user", "id", orderItemDTO.getUserId(), JoinType.LEFT));
        orderSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "shop", "id", productTypeEntity.getProduct().getShop().getId(), JoinType.LEFT));
        orderSpecification.add(new SearchCriteria("status", Common.ORDER_STATUS_IN_CART, SearchOperation.EQUAL));
        OrderEntity orderEntity = orderRepository.findOne(orderSpecification).orElseGet(() -> {
            OrderEntity order = new OrderEntity();
            order.setShop(productTypeEntity.getProduct().getShop());
            order.setUser(userEntity);
            order.setStatus(Common.ORDER_STATUS_IN_CART);
            order = orderRepository.save(order);
            return order;
        });

        GenericSpecification<OrderItemEntity> orderItemSpecification = new GenericSpecification<OrderItemEntity>();
        orderItemSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "productType", "id" , orderItemDTO.getProductTypeId(), JoinType.INNER));
        orderItemSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "order", "id", orderEntity.getId(), JoinType.INNER));
        OrderItemEntity orderItemEntity = orderItemRepository.findOne(orderItemSpecification).orElseGet(OrderItemEntity::new);
        Long quantity = orderItemEntity.getQuantity() + orderItemDTO.getQuantity();

        if (quantity <= 0) {
            orderItemRepository.deleteById(orderItemEntity.getId());
            orderEntity.getOrderItems().remove(orderItemEntity);
        } else {
            orderItemEntity.setQuantity(quantity);
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setProductType(productTypeEntity);

            orderItemRepository.save(orderItemEntity);

            orderEntity.getOrderItems().add(orderItemEntity);
        }

        List<OrderDTO> orderDTOs = formatResponseData(orderEntity.getOrderItems().stream().toList());
        if (orderDTOs.isEmpty()) {
            return null;
        } else {
            return orderDTOs.get(0);
        }
    }

    private ProductResponseDTO mapAndHandleSaleProgram(ProductEntity productEntity) {
        SaleProgramEntity saleProgram = productEntity.getSaleProgram();
        ProductResponseDTO productResponse = mapper.map(productEntity, ProductResponseDTO.class);
        if (saleProgram != null) {
            if (saleProgram.getEndTime() > System.currentTimeMillis()) {
                productResponse.getProductTypes().forEach(productTypeDTO -> productTypeDTO.setDiscountPrice(productTypeDTO.getPrice() - Math.round(productTypeDTO.getPrice() * (double) saleProgram.getDiscount())));
            }
        }
        if (productEntity.getImageEntities().get(0) != null) {
            productResponse.setImages(List.of(productEntity.getImageEntities().get(0).getUrl()));
        }
        productResponse.getShop().setAvatar(productEntity.getShop().getUserLoginData().getAvatarUrl());
        return productResponse;
    }

    private List<OrderDTO> formatResponseData(List<OrderItemEntity> orderItemEntities) {

        Map<Long, ProductTypeEntity> productTypeEntityMap = new HashMap<>();
        orderItemEntities.forEach(orderItemEntity -> {
            ProductTypeEntity productTypeEntity = productTypeEntityMap.get(orderItemEntity.getProductType().getId());
            if (productTypeEntity == null) {
                productTypeEntity =  orderItemEntity.getProductType();
                productTypeEntity.setQuantity(orderItemEntity.getQuantity());
            } else {
                Long quantity = productTypeEntity.getQuantity() + orderItemEntity.getQuantity();
                productTypeEntity.setQuantity(quantity);
            }
            productTypeEntityMap.put(
                    orderItemEntity.getProductType().getId(),
                    productTypeEntity
            );
        });

        Map<Long, OrderEntity> orderEntityMap = new HashMap<>();
        orderItemEntities.forEach(orderItemEntity -> {
            orderEntityMap.put(
                    orderItemEntity.getOrder().getId(),
                    orderItemEntity.getOrder()
            );
        });

        Map<Long, ProductEntity> productEntityMap = new HashMap<>();
        productTypeEntityMap.values().forEach(productTypeEntity -> {
            ProductEntity productEntity = productEntityMap.get(productTypeEntity.getProduct().getId());
            if (productEntity == null) {
                productEntity = productTypeEntity.getProduct();
                productEntity.setProductTypes(new ArrayList<>());
            }

            productEntity.getProductTypes().add(productTypeEntity);

            productEntityMap.put(
                    productTypeEntity.getProduct().getId(),
                    productEntity
            );
        });

        Map<Long, ShopEntity> shopEntityMap = new HashMap<>();
        productEntityMap.values().forEach(productEntity -> {
            ShopEntity shopEntity = shopEntityMap.get(productEntity.getShop().getId());
            if (shopEntity == null) {
                shopEntity = productEntity.getShop();
                shopEntity.setProductEntities(new ArrayList<>());
            }

            shopEntity.getProductEntities().add(productEntity);

            shopEntityMap.put(
                    productEntity.getShop().getId(),
                    shopEntity
            );
        });

        Map<Long, ShopDTO> shopDTOMap = new HashMap<>();
        List<ShopDTO> shopDTOs = shopEntityMap.values().stream().map(shopEntity -> {
            ShopDTO shopDTO = mapper.map(shopEntity, ShopDTO.class);
            List<ProductResponseDTO> productResponseDTOs = shopEntity.getProductEntities().stream().map(this::mapAndHandleSaleProgram).toList();
            shopDTO.setProductDTOList(productResponseDTOs);
            shopDTO.setAvatar(shopEntity.getUserLoginData().getAvatarUrl());
            return shopDTO;
        }).toList();

        shopDTOs.forEach(shopDTO -> {
            shopDTOMap.put(shopDTO.getId(), shopDTO);
        });

        List<OrderDTO> orderDTOs = new ArrayList<>();
        orderEntityMap.values().forEach(orderEntity -> {
            OrderDTO orderDTO = mapper.map(orderEntity, OrderDTO.class);
            orderDTO.setShop(shopDTOMap.get(orderEntity.getShop().getId()));
            setUserDTO(orderDTO, orderEntity);
            orderDTOs.add(orderDTO);
        });

        return orderDTOs;
    }

    private OrderDTO setUserDTO(OrderDTO orderDTO, OrderEntity orderEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setAvatarUrl(orderEntity.getUser().getAvatarUrl());
        userDTO.setId(orderEntity.getUser().getId());
        userDTO.setFirstName(orderEntity.getUser().getFirstName());
        userDTO.setLastName(orderEntity.getUser().getLastName());
        orderDTO.setUserDTO(userDTO);
        return orderDTO;
    }

}
