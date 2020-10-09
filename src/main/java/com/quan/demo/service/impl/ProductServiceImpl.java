package com.quan.demo.service.impl;

import com.quan.demo.models.Product;
import com.quan.demo.models.TypeProduct;
import com.quan.demo.repository.ProductRepository;
import com.quan.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findAllByName(String regex, Pageable pageable) {
        return productRepository.findAllByNameContaining(regex, pageable);
    }

    @Override
    public Page<Product> findAllByTypeProduct(TypeProduct typeProduct, Pageable pageable) {
        return productRepository.findAllByTypeProduct(typeProduct, pageable);
    }

    @Override
    public Product findOne(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
