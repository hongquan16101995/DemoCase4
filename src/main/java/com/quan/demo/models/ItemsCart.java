package com.quan.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemsCart {
    private Product product;
    private int quantity;
    private double total;
}
