package com.quan.demo.service;

import com.quan.demo.models.OrdersDetail;

public interface OrdersDetailService {
    void saveOrdersDetail(OrdersDetail ordersDetail);

    Iterable<OrdersDetail> findOrdersDetailById_Order(Long id);
}
