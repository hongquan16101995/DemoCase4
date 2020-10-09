package com.quan.demo.service.impl;

import com.quan.demo.models.TypeProduct;
import com.quan.demo.repository.TypeProductRepository;
import com.quan.demo.service.TypeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypeProductServiceImpl implements TypeProductService {
    @Autowired
    private TypeProductRepository typeProductRepository;
    @Override
    public Iterable<TypeProduct> findAll() {
        return typeProductRepository.findAll();
    }

    @Override
    public TypeProduct getTypeProduct(Long id) {
        return typeProductRepository.findById(id).orElse(null);
    }
}
