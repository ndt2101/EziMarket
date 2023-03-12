package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;
import com.ndt2101.ezimarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/order")
public class OrderController extends BaseController<Object> {

    @Autowired
    private OrderService orderService;

    @PostMapping("/cart")
    public ResponseEntity<?> addToCart(@RequestBody OrderItemDTO orderItemDTO) {
        return successfulResponse(orderService.addToCart(orderItemDTO));
    }

    @PutMapping("/checkout")
    public ResponseEntity<?> checkOutCart(@RequestBody List<OrderDTO> orderDTOs) {
        return successfulResponse(orderService.checkOutCart(orderDTOs));
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmOrder(@PathVariable(name = "id") Long id) throws ParseException, ExecutionException, InterruptedException, CloneNotSupportedException {
        return successfulResponse(orderService.confirmOrder(id));
    }
    @PutMapping("status/{orderId}/{status}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable(name = "orderId") Long orderId, @PathVariable(name = "status") String status) {
        return successfulResponse(orderService.updateOrderStatus(orderId, status));
    }

}
