package com.haidilao.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("订单不存在: id=" + orderId);
    }

    public OrderNotFoundException(String orderNo) {
        super("订单不存在: orderNo=" + orderNo);
    }
}
