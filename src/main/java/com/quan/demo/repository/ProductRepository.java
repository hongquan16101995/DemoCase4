package com.quan.demo.repository;

import com.quan.demo.models.Product;
import com.quan.demo.models.TypeProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    Page<Product> findAllByNameContaining(String regex, Pageable pageable);

    Page<Product> findAllByTypeProduct(TypeProduct typeProduct, Pageable pageable);

    Product findByName(String name);
}
