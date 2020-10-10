package com.quan.demo.repository;

import com.quan.demo.models.OrdersDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersDetailRepository extends CrudRepository<OrdersDetail, Long> {
    Iterable<OrdersDetail> findAllByIdorder(Long id);
}
