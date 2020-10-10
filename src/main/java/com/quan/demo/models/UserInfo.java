package com.quan.demo.models;

import lombok.Data;
import org.hibernate.mapping.ToOne;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String account;
    private String password;
    private String name;
    private int age;
    private String render;
    private String address;
    private String phone;
    private Date datecreated;
    private String avatar;

    @Transient
    private MultipartFile image;

    @ManyToOne
    @JoinColumn(name = "idrole")
    private Roles roles;
}
