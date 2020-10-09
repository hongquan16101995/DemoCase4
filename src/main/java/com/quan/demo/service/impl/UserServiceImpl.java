package com.quan.demo.service.impl;

import com.quan.demo.models.Roles;
import com.quan.demo.models.UserInfo;
import com.quan.demo.repository.UserRepository;
import com.quan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserInfo> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<UserInfo> findAllByName(String regex, Pageable pageable) {
        return userRepository.findAllByNameContaining(regex, pageable);
    }

    @Override
    public Page<UserInfo> findByDateCreated(Date date, Pageable pageable) {
        return userRepository.findAllByDatecreated(date,pageable);
    }

    @Override
    public UserInfo findOne(String account) {
        return userRepository.findByAccount(account);
    }

    @Override
    public void saveUser(UserInfo userInfo) {
        userRepository.save(userInfo);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByAccount(username);
        List<Roles> roles = new ArrayList<>();
        roles.add(userInfo.getRoles());

        User user = new User(userInfo.getAccount(), userInfo.getPassword(), roles);
        return user;
    }
}
