package com.quan.demo.service;

import com.quan.demo.models.Roles;
import com.quan.demo.models.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;

public interface UserService {
    Page<UserInfo> findAll(Pageable pageable);

    Page<UserInfo> findAllByRoles(Roles roles, Pageable pageable);

    Page<UserInfo> findAllByName(String regex, Roles roles, Pageable pageable);

    UserInfo findOne(String account);

    void saveUser(UserInfo userInfo);

    void deleteUser(Long id);
}
