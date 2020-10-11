package com.quan.demo.service;

import com.quan.demo.models.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface OrdersService {
    void saveOrders(Orders orders);

    Page<Orders> findAll(Pageable pageable);

    Page<Orders> findByAccountUser(String account, Pageable pageable);
}
