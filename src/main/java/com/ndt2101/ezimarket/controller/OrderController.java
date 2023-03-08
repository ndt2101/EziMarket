package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.OrderItemDTO;
import com.ndt2101.ezimarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController extends BaseController<Object> {

    @Autowired
    private OrderService orderService;

    @PostMapping("/cart")
    public ResponseEntity<?> addToCart(@RequestBody OrderItemDTO orderItemDTO) {
        return successfulResponse(orderService.addToCart(orderItemDTO));
    }
}
