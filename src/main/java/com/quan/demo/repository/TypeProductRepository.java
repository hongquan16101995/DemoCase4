package com.quan.demo.repository;

import com.quan.demo.models.TypeProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeProductRepository extends CrudRepository<TypeProduct, Long> {
}
