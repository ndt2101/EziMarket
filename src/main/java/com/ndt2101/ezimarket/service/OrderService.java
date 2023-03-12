package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderService {
    OrderDTO addToCart(OrderItemDTO orderItemDTO);
    List<OrderDTO> checkOutCart(List<OrderDTO> orderDTOs);
    String confirmOrder(Long orderId) throws ExecutionException, InterruptedException, ParseException, CloneNotSupportedException;
    OrderDTO updateOrderStatus(Long orderId, String orderStatus);

    OrderDTO paypalCheckout(OrderDTO orderDTO);
}
