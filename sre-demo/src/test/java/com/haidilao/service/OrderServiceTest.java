package com.haidilao.service;

import com.haidilao.model.Order;
import com.haidilao.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Long existingOrderId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        Order order = new Order();
        order.setOrderNo("HDL-20260701-001");
        order.setCustomerName("张三");
        order.setCustomerPhone("13800138000");
        order.setStoreName("望京SOHO店");
        order.setTotalAmount(new BigDecimal("523.00"));
        order.setStatus("COMPLETED");
        order.setTableNo(12);
        order.setGuestCount(4);

        Order saved = orderRepository.save(order);
        existingOrderId = saved.getId();
    }

    @Test
    void testGetOrderDetail_success() {
        OrderService.OrderDetailDTO dto = orderService.getOrderDetail(existingOrderId);

        assertNotNull(dto);
        assertEquals("HDL-20260701-001", dto.getOrderNo());
        assertEquals("张三", dto.getCustomerName());
        assertEquals("望京SOHO店", dto.getStoreName());
        assertEquals(523, dto.getEarnedPoints());
    }

    /**
     * 这个测试会失败！
     * 因为 OrderService.getOrderDetail() 在订单不存在时会抛出 NullPointerException
     * 预期行为：应该抛出自定义的 OrderNotFoundException 或返回 404
     */
    @Test
    void testGetOrderDetail_notFound_shouldThrowNPE() {
        // 使用一个不存在的 ID
        assertThrows(NullPointerException.class, () -> {
            orderService.getOrderDetail(999L);
        });
    }

    @Test
    void testGetOrderByOrderNo_success() {
        OrderService.OrderDetailDTO dto = orderService.getOrderByOrderNo("HDL-20260701-001");

        assertNotNull(dto);
        assertEquals("张三", dto.getCustomerName());
    }

    /**
     * 同样会触发 NullPointerException
     */
    @Test
    void testGetOrderByOrderNo_notFound_shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> {
            orderService.getOrderByOrderNo("NOT-EXIST-ORDER");
        });
    }
}
