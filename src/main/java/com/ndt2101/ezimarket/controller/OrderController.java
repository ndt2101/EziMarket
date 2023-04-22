package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.GHN.ShippingCalculate;
import com.ndt2101.ezimarket.dto.OrderDTO;
import com.ndt2101.ezimarket.dto.OrderItemDTO;
import com.ndt2101.ezimarket.model.OrderEntity;
import com.ndt2101.ezimarket.service.OrderService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> updateOrderStatus(@PathVariable(name = "orderId") Long orderId, @PathVariable(name = "status") String status) throws ExecutionException, InterruptedException {
        return successfulResponse(orderService.updateOrderStatus(orderId, status));
    }

    @GetMapping("/")
    public ResponseEntity<?> getOrders(
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request
    ) {
        GenericSpecification<OrderEntity> specification = new GenericSpecification<OrderEntity>().getBasicQuery(request);
        if (shopId != null) {
            JoinCriteria joinCriteria = new JoinCriteria(SearchOperation.EQUAL, "shop", "id", shopId, JoinType.INNER);
            specification.buildJoin(joinCriteria);
        }

        if (userId != null) {
            JoinCriteria joinCriteria = new JoinCriteria(SearchOperation.EQUAL, "user", "id", userId, JoinType.INNER);
            specification.buildJoin(joinCriteria);
        }
        return this.resPagination(orderService.getOrders(page, perPage, specification, type));
    }

    @PostMapping("/calculate_fee/{id}")
    public ResponseEntity<?> calculateOrderFee(@PathVariable("id") Long shopId, @RequestBody ShippingCalculate shippingCalculate) throws ExecutionException, InterruptedException {
        return this.successfulResponse(orderService.calculateOrderFee(shippingCalculate, shopId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardParameter(@RequestParam("id") Long shopId) {
        return this.successfulResponse(orderService.getDashboardParameter(shopId));
    }

}
