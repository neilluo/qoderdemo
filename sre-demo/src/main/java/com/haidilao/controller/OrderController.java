package com.haidilao.controller;

import com.haidilao.model.Order;
import com.haidilao.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 根据ID查询订单详情
     * 当订单不存在时会触发 NullPointerException（已知 BUG）
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderService.OrderDetailDTO> getOrderDetail(@PathVariable Long id) {
        OrderService.OrderDetailDTO detail = orderService.getOrderDetail(id);
        return ResponseEntity.ok(detail);
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/by-no/{orderNo}")
    public ResponseEntity<OrderService.OrderDetailDTO> getOrderByNo(@PathVariable String orderNo) {
        OrderService.OrderDetailDTO detail = orderService.getOrderByOrderNo(orderNo);
        return ResponseEntity.ok(detail);
    }

    /**
     * 查询所有订单
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }
}
