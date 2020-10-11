package com.quan.demo.repository;

import com.quan.demo.models.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface OrdersRepository extends PagingAndSortingRepository<Orders, Long> {
    Page<Orders> findAllByAccountuserContaining(String account, Pageable pageable);
}
