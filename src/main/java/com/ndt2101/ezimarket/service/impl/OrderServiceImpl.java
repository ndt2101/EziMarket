package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.*;
import com.ndt2101.ezimarket.dto.GHN.*;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.model.paypal.Payer;
import com.ndt2101.ezimarket.model.paypal.PaymentMethod;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.OrderService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.JoinType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    private VoucherRepository voucherRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private PayerRepository payerRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RefundRepository refundRepository;
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
        orderItemSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "productType", "id", orderItemDTO.getProductTypeId(), JoinType.INNER));
        orderItemSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "order", "id", orderEntity.getId(), JoinType.INNER));
        OrderItemEntity orderItemEntity = orderItemRepository.findOne(orderItemSpecification).orElseGet(OrderItemEntity::new);
        if (orderItemEntity.getItemQuantity() == null) {
            orderItemEntity.setItemQuantity(0L);
        }
        Long quantity = orderItemEntity.getItemQuantity() + orderItemDTO.getItemQuantity();

        if (quantity <= 0 && orderItemEntity.getId() != null) {
            orderItemRepository.deleteById(orderItemEntity.getId());
            orderEntity.getOrderItems().remove(orderItemEntity);
        } else {
            orderItemEntity.setItemQuantity(quantity);
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

    @Override
    public List<OrderDTO> checkOutCart(List<OrderDTO> orderDTOs) {
        UserLoginDataEntity userEntity = userRepository.findById(orderDTOs.get(0).getUserDTO().getId()).orElseThrow(Common.userNotFound);
        List<OrderItemEntity> confirmedOrderItems = new ArrayList<>();

        orderDTOs.forEach(orderDTO -> {
            OrderEntity orderEntity = orderRepository.findById(orderDTO.getId()).orElseThrow(Common.orderNotFound);
            Set<Long> tillInCartProductTypeIds = orderEntity.getOrderItems().stream().map(orderItemEntity -> orderItemEntity.getProductType().getId()).collect(Collectors.toSet());
            ShopEntity shopEntity = shopRepository.findById(orderDTO.getShop().getId()).orElseThrow(Common.shopNotFound);

            orderDTO.getShop().getProductDTOList().forEach(productResponseDTO -> {
                productResponseDTO.getProductTypes().forEach(productTypeDTO -> {
                    tillInCartProductTypeIds.remove(productTypeDTO.getId()); // ket qua sau cung se la la cac productType chua duoc cormfirm(van trong cart)
                });
            });

            Set<OrderItemEntity> tillInCartOrderItem = orderEntity.getOrderItems().stream().filter(orderItemEntity -> tillInCartProductTypeIds.contains(orderItemEntity.getProductType().getId())).collect(Collectors.toSet());

            if (!tillInCartOrderItem.isEmpty()) {
                OrderEntity newCart = new OrderEntity();
                newCart.setShop(shopEntity);
                newCart.setUser(userEntity);
                newCart.setStatus(Common.ORDER_STATUS_IN_CART);
                newCart = orderRepository.save(newCart);

                OrderEntity finalNewCart = newCart;
                tillInCartOrderItem.forEach(orderItemEntity -> {
                    orderItemEntity.setOrder(finalNewCart);
                    orderItemRepository.save(orderItemEntity);
                    orderEntity.getOrderItems().remove(orderItemEntity);
                });
            }
            OrderEntity savedOrderEntity = setupForOrderEntity(orderEntity, orderDTO);
            confirmedOrderItems.addAll(orderRepository.save(savedOrderEntity).getOrderItems());
        });
        return formatResponseData(confirmedOrderItems);
    }

    @Override
    @Transactional
    public String confirmOrder(Long orderId) throws ExecutionException, InterruptedException, ParseException {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(Common.orderNotFound);
        MappedOrderEntity mappedOrderEntity = mapper.map(orderEntity, MappedOrderEntity.class);

        OrderEntity cloneOrderEntity = mapper.map(mappedOrderEntity, OrderEntity.class);
        OrderData ghnOrder = createGHNOrder(cloneOrderEntity);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ShippingMethod shippingMethod = orderEntity.getShippingMethod();
        shippingMethod.setReceivedDay(sdf.parse(ghnOrder.getData().getExpected_delivery_time()));
        shippingMethod.setPrice((long) ghnOrder.getData().getTotal_fee());

        shippingMethod = shippingMethodRepository.save(shippingMethod);
        orderEntity.setShippingMethod(shippingMethod);

        orderEntity.setCode(ghnOrder.getData().getOrder_code());
        orderEntity.setTotalPrice(ghnOrder.getData().getTotal_fee() + orderEntity.getTotalPrice());
        if (orderEntity.getPaymentMethod().getId() == 1){
            orderEntity.setStatus(Common.ORDER_STATUS_PICKING);
        } else {
            orderEntity.setStatus(Common.ORDER_STATUS_PAYING);
        }
        OrderEntity finalOrderEntity = orderEntity;
        Map<Long, ProductTypeEntity> productTypeEntities = new HashMap<>();
        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            ProductTypeEntity productType = productTypeRepository.findById(orderItemEntity.getProductType().getId()).orElseThrow(Common.productTypeNotFound);
            long newQuantity = productType.getQuantity() - orderItemEntity.getItemQuantity();
            productType.setQuantity(newQuantity);
            productType = productTypeRepository.save(orderItemEntity.getProductType());
            productTypeEntities.put(orderItemEntity.getId(), productType);
        });

        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            orderItemEntity.setProductType(productTypeEntities.get(orderItemEntity.getId()));
        });

        orderEntity = orderRepository.save(finalOrderEntity);
        return orderEntity.getCode();
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String orderStatus) throws ExecutionException, InterruptedException { // only for status cancel, delivering, received
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(Common.orderNotFound);
        if (orderStatus.equals(Common.ORDER_STATUS_CANCELED)) {
//            TODO: base on current status to handle quantity of product type in order
            String currentStatus = orderEntity.getStatus();
            if (currentStatus.equals(Common.ORDER_STATUS_DELIVERING) || currentStatus.equals(Common.ORDER_STATUS_PICKING) || currentStatus.equals(Common.ORDER_STATUS_PAYING)) {
                String cancelGHNOrderStatus = cancelGHNOrder(orderEntity.getCode(), String.valueOf(orderEntity.getShop().getGHNStoreId()));
                if (cancelGHNOrderStatus.equals("OK")){
                    orderEntity.getOrderItems().forEach(orderItemEntity -> {
                        ProductTypeEntity productType = productTypeRepository.findById(orderItemEntity.getProductType().getId()).orElseThrow(Common.productTypeNotFound);
                        long newQuantity = productType.getQuantity() + orderItemEntity.getItemQuantity();
                        productType.setQuantity(newQuantity);
                        productTypeRepository.save(productType);
                    });
                    orderEntity.setStatus(Common.ORDER_STATUS_CANCELED);
                }
            } else if (currentStatus.equals(Common.ORDER_STATUS_CONFIRMING)) {
                orderEntity.setStatus(Common.ORDER_STATUS_CANCELED);
            }
        }
        if (orderStatus.equals(Common.ORDER_STATUS_DELIVERING) && orderEntity.getStatus().equals(Common.ORDER_STATUS_PICKING)) {
            orderEntity.setStatus(Common.ORDER_STATUS_DELIVERING);
        }
        if (orderStatus.equals(Common.ORDER_STATUS_RECEIVED) && orderEntity.getStatus().equals(Common.ORDER_STATUS_DELIVERING)) {
            orderEntity.setStatus(Common.ORDER_STATUS_RECEIVED);
        }
        // save order after update to database
        orderEntity = orderRepository.save(orderEntity);
        OrderDTO orderDTO = formatResponseData(orderEntity.getOrderItems().stream().toList()).get(0);
        return orderDTO;
    }

    @Override
    public PaginateDTO<OrderDTO> getOrders(Integer page, Integer perPage, GenericSpecification<OrderEntity> specification) {
        PaginateDTO<OrderEntity> orderEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderEntityPaginateDTO.getPageData().forEach(orderEntity ->
                orderItems.addAll(orderEntity.getOrderItems())
        );
        Page<OrderDTO> pageData = new PageImpl<>(formatResponseData(orderItems), PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, orderEntityPaginateDTO.getPagination());
    }

    private String cancelGHNOrder(String orderCode, String ghnShopId) throws ExecutionException, InterruptedException {
        List<String> order_codes = new ArrayList<>();
        order_codes.add(orderCode);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<String> createStore = new Callable<String>() {
            @Override
            public String call() throws Exception {
                RestTemplate restTemplate = new RestTemplate();
                // Tạo header
                HttpHeaders headers = new HttpHeaders();
                headers.set("Token", Common.GHN_TOKEN);
                headers.set("ShopId", ghnShopId);
                headers.set("Content-Type", Common.GHN_CONTENT_TYPE);
                // Tạo entity từ đối tượng request và header
                HttpEntity<CancelOrderPayload> entity = new HttpEntity<>(new CancelOrderPayload(order_codes), headers);
                ResponseEntity<GHNResponse> savedStore = restTemplate.exchange(Common.CANCEL_ORDER_IN_GHN_API,  HttpMethod.POST, entity, GHNResponse.class);
                return (String) ((ArrayList<LinkedHashMap>) savedStore.getBody().getData()).get(0).get("message");
            }
        };
        Future<String> future = executorService.submit(createStore);
        return future.get();
    }

    /* TODO: chua lam
     * check payer co ton tai hay khong
     * neu khong thi luu payer truoc
     * sau do save transaction
     * khi tra lai thi save refund, save refund thi cac truong da xo du het, chi can lay ra de set
     */
    @Override
    public OrderDTO paypalCheckout(OrderDTO orderDTO) {
        OrderEntity orderEntity = orderRepository.findById(orderDTO.getId()).orElseThrow(Common.orderNotFound);
        Payer formPayer = payerRepository.findByUserLoginDataEntity_Id(orderDTO.getUserDTO().getId()).orElse(orderDTO.getPayment().getFrom());
        return null;
    }


    private OrderData createGHNOrder(OrderEntity orderEntity) throws ExecutionException, InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<OrderData> createStore = new Callable<OrderData>() {
            @Override
            public OrderData call() throws Exception {
                RestTemplate restTemplate = new RestTemplate();
                // Tạo header
                HttpHeaders headers = new HttpHeaders();
                headers.set("Token", Common.GHN_TOKEN);
                headers.set("ShopId", String.valueOf(orderEntity.getShop().getGHNStoreId()));
                headers.set("Content-Type", Common.GHN_CONTENT_TYPE);
                // Tạo entity từ đối tượng request và header
                HttpEntity<CreateOrderPayload> entity = new HttpEntity<>(CreateOrderPayload.createOrderPayload(orderEntity), headers);
                ResponseEntity<OrderData> savedStore = restTemplate.exchange(Common.CREATE_ORDER_IN_GHN_API,  HttpMethod.POST, entity, OrderData.class);
                return savedStore.getBody();
            }
        };
        Future<OrderData> future = executorService.submit(createStore);
        return future.get();
    }

    private OrderEntity setupForOrderEntity(OrderEntity orderEntity, OrderDTO orderDTO) {
        AtomicLong totalPrice = new AtomicLong(0L);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderDTO.getPaymentMethod().getId()).orElseThrow(Common.paymentMethodNotFound);

        VoucherEntity voucherEntity = null;
        if (orderDTO.getVoucherId() != null) {
            GenericSpecification<VoucherEntity> voucherSpecification = new GenericSpecification<VoucherEntity>();
            voucherSpecification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "users", "id", orderDTO.getUserDTO().getId(), JoinType.LEFT));
            voucherSpecification.add(new SearchCriteria("id", orderDTO.getVoucherId(), SearchOperation.EQUAL));
            voucherEntity = voucherRepository.findOne(voucherSpecification).orElseThrow(Common.voucherNotFound);
        }

        List<OrderItemEntity> checkOutOfStock = orderEntity.getOrderItems().stream().toList();

        checkOutOfStock.forEach(orderItemEntity -> {
            if (orderItemEntity.getProductType().getQuantity() <= 0) {
                orderItemRepository.deleteById(orderItemEntity.getId());
                productTypeRepository.deleteById(orderItemEntity.getProductType().getId());
                orderEntity.getOrderItems().remove(orderItemEntity);
            }
        });

        orderEntity.getOrderItems().forEach(orderItemEntity -> {
            SaleProgramEntity saleProgramEntity = orderItemEntity.getProductType().getProduct().getSaleProgram();
            if (saleProgramEntity != null && saleProgramEntity.getEndTime() > System.currentTimeMillis()) {
                totalPrice.addAndGet(Math.round(orderItemEntity.getItemQuantity() * (orderItemEntity.getProductType().getPrice() - orderItemEntity.getProductType().getPrice() * saleProgramEntity.getDiscount())));
            } else {
                totalPrice.addAndGet(orderItemEntity.getItemQuantity() * orderItemEntity.getProductType().getPrice());
            }
        });
        if (voucherEntity != null && totalPrice.get() >= voucherEntity.getPriceCondition() && voucherEntity.getEndTime() > System.currentTimeMillis() && Objects.equals(voucherEntity.getShop().getId(), orderEntity.getShop().getId())) {
            totalPrice.getAndSet(Math.round(totalPrice.get() - totalPrice.get() * voucherEntity.getDiscount()));
            voucherRepository.deleteUserVoucher(orderDTO.getUserDTO().getId(), orderDTO.getVoucherId());
        }

        orderEntity.setStatus(Common.ORDER_STATUS_CONFIRMING);
        orderEntity.setTotalPrice(totalPrice.get());
        orderEntity.setNoteToShop(orderDTO.getNoteToShop());
        orderEntity.setShipTo(addressRepository.findById(orderDTO.getShipTo().getId()).orElseThrow(Common.addressNotFound));
        orderEntity.setPaymentMethod(paymentMethod);
        orderEntity.setShippingMethod(shippingMethodRepository.save(orderDTO.getShippingMethod()));
        return orderEntity;
    }

//    TODO: chua test voi truong hop ap dung voucher va san pham co ct khuyen mai

    private ProductResponseDTO mapAndHandleSaleProgram(ProductEntity productEntity) {
        SaleProgramEntity saleProgram = productEntity.getSaleProgram();
        ProductResponseDTO productResponse = mapper.map(productEntity, ProductResponseDTO.class);
        if (saleProgram != null) {
            if (saleProgram.getEndTime() > System.currentTimeMillis()) {
                productResponse.getProductTypes().forEach(productTypeDTO -> productTypeDTO.setDiscountPrice(productTypeDTO.getPrice() - Math.round(productTypeDTO.getPrice() * (double) saleProgram.getDiscount())));
            }
        }
        if (productEntity.getImageEntities().get(0) != null) {
            productResponse.setImages(List.of(mapper.map(productEntity.getImageEntities().get(0), ImageDTO.class)));
        }
        productResponse.getShop().setAvatar(productEntity.getShop().getUserLoginData().getAvatarUrl());
        return productResponse;
    }

    private List<OrderDTO> formatResponseData(List<OrderItemEntity> orderItemEntities) {

        Map<Long, ProductTypeEntity> productTypeEntityMap = new HashMap<>();
        orderItemEntities.forEach(orderItemEntity -> {
            ProductTypeEntity productTypeEntity = productTypeEntityMap.get(orderItemEntity.getProductType().getId());
            if (productTypeEntity == null) {
                productTypeEntity = orderItemEntity.getProductType();
                productTypeEntity.setQuantity(orderItemEntity.getItemQuantity());
            } else {
                Long quantity = productTypeEntity.getQuantity() + orderItemEntity.getItemQuantity();
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
            if (orderDTO.getShippingMethod() != null) {
                orderDTO.getShippingMethod().setOrder(null);
            }
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

    /**
     * Xóa item order -> addToCart quantity - current quantity (để quantity sau cùng = 0)
     */
}
