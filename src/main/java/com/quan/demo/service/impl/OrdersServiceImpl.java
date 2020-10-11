package com.quan.demo.service.impl;

import com.quan.demo.models.Orders;
import com.quan.demo.repository.OrdersRepository;
import com.quan.demo.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public void saveOrders(Orders orders) {
        ordersRepository.save(orders);
    }

    @Override
    public Page<Orders> findAll(Pageable pageable) {
        return ordersRepository.findAll(pageable);
    }

    @Override
    public Page<Orders> findByAccountUser(String account, Pageable pageable) {
        return ordersRepository.findAllByAccountuserContaining(account, pageable);
    }
}
