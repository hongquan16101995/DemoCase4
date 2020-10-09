package com.quan.demo.service;

import com.quan.demo.models.Product;
import com.quan.demo.models.TypeProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByName(String regex, Pageable pageable);

    Page<Product> findAllByTypeProduct(TypeProduct typeProduct, Pageable pageable);

    Product findOne(Long id);

    Product findByName(String name);

    void saveProduct(Product product);

    void deleteProduct(Long id);
}
