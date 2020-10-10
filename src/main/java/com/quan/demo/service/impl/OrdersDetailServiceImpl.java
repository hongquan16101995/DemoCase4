package com.quan.demo.service.impl;

import com.quan.demo.models.OrdersDetail;
import com.quan.demo.repository.OrdersDetailRepository;
import com.quan.demo.service.OrdersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl implements OrdersDetailService {
    @Autowired
    private OrdersDetailRepository ordersDetailRepository;

    @Override
    public void saveOrdersDetail(OrdersDetail ordersDetail) {
        ordersDetailRepository.save(ordersDetail);
    }

    @Override
    public Iterable<OrdersDetail> findOrdersDetailById_Order(Long id) {
        return ordersDetailRepository.findAllByIdorder(id);
    }
}
