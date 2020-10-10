package com.quan.demo.repository;

import com.quan.demo.models.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends PagingAndSortingRepository<Orders, Long> {
    Page<Orders> findAllByAccountuser(String account, Pageable pageable);
}
