package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;

import java.util.List;

public interface OrderService {
    OrderDTO addToCart(OrderItemDTO orderItemDTO);
    List<OrderDTO> checkOutCart(List<OrderDTO> orderDTOs);
}
