package com.quan.demo.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.*;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double price;
    private String avatar;
    private String description;
    private int amount;

    @Transient
    private MultipartFile image;

    @ManyToOne
    @JoinColumn(name = "id_type")
    private TypeProduct typeProduct;
}
