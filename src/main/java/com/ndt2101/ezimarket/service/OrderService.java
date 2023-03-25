package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.GHN.ShippingCalculate;
import com.ndt2101.ezimarket.dto.GHN.ShippingCalculateResponse;
import com.ndt2101.ezimarket.dto.GHN.ShippingCalculateResponseData;
import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.OrderEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderService {
    OrderDTO addToCart(OrderItemDTO orderItemDTO);
    List<OrderDTO> checkOutCart(List<OrderDTO> orderDTOs);
    String confirmOrder(Long orderId) throws ExecutionException, InterruptedException, ParseException, CloneNotSupportedException;
    OrderDTO updateOrderStatus(Long orderId, String orderStatus) throws ExecutionException, InterruptedException;
    PaginateDTO<OrderDTO> getOrders(Integer page, Integer perPage, GenericSpecification<OrderEntity> specification);
    OrderDTO paypalCheckout(OrderDTO orderDTO);

    ShippingCalculateResponseData calculateOrderFee(ShippingCalculate shippingCalculate, Long shopId) throws ExecutionException, InterruptedException;
}
