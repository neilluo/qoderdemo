package com.haidilao.service;

import com.haidilao.exception.OrderNotFoundException;
import com.haidilao.model.Order;
import com.haidilao.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 根据订单ID查询订单详情
     */
    public OrderDetailDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setCustomerName(order.getCustomerName());
        dto.setStoreName(order.getStoreName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setTableNo(order.getTableNo());
        dto.setGuestCount(order.getGuestCount());
        dto.setCreatedAt(order.getCreatedAt());

        // 计算消费积分
        dto.setEarnedPoints(calculatePoints(order));

        return dto;
    }

    /**
     * 根据订单号查询
     */
    public OrderDetailDTO getOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new OrderNotFoundException(orderNo));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setCustomerName(order.getCustomerName());
        dto.setStoreName(order.getStoreName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        return dto;
    }

    /**
     * 查询所有订单
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 创建新订单
     */
    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    /**
     * 计算积分（堂食: 1元=1积分）
     */
    private int calculatePoints(Order order) {
        if (order.getTotalAmount() == null) {
            return 0;
        }
        return order.getTotalAmount().intValue();
    }

    // DTO 内部类
    public static class OrderDetailDTO {
        private String orderNo;
        private String customerName;
        private String storeName;
        private BigDecimal totalAmount;
        private String status;
        private Integer tableNo;
        private Integer guestCount;
        private LocalDateTime createdAt;
        private int earnedPoints;

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getTableNo() { return tableNo; }
        public void setTableNo(Integer tableNo) { this.tableNo = tableNo; }

        public Integer getGuestCount() { return guestCount; }
        public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public int getEarnedPoints() { return earnedPoints; }
        public void setEarnedPoints(int earnedPoints) { this.earnedPoints = earnedPoints; }
    }
}
