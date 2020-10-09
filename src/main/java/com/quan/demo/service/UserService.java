package com.quan.demo.service;

import com.quan.demo.models.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;

public interface UserService {
    Page<UserInfo> findAll(Pageable pageable);

    Page<UserInfo> findAllByName(String regex, Pageable pageable);

    Page<UserInfo> findByDateCreated(Date date, Pageable pageable);

    UserInfo findOne(String account);

    void saveUser(UserInfo userInfo);

    void deleteUser(Long id);
}
