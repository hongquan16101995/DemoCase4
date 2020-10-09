package com.quan.demo.controller;

import com.quan.demo.models.Product;
import com.quan.demo.models.UserInfo;
import com.quan.demo.service.ProductService;
import com.quan.demo.service.RolesService;
import com.quan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private Environment environment;

    @GetMapping("/")
    public ModelAndView listProduct(@RequestParam("regex") Optional<String> regex,
                                    @SortDefault(value = {"id"}) @PageableDefault(value = 20)Pageable pageable){
        Page<Product> products;
        ModelAndView modelAndView = new ModelAndView("home/index");
        if (regex.isPresent()) {
            products = productService.findAllByName(regex.get(), pageable);
        } else {
            products = productService.findAll(pageable);
        }
        modelAndView.addObject("regex", regex.orElse(""));
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/accessDenied")
    public ModelAndView accessDenied(){
        return new ModelAndView("home/accessDenied");
    }

    @GetMapping("/registration")
    public ModelAndView registrationUser(){
        return new ModelAndView("home/registration", "user", new UserInfo());
    }

    @PostMapping("/registration")
    public ModelAndView registrationAccount(@ModelAttribute("user") UserInfo userInfo,
                                            @SortDefault(value = {"id"}) @PageableDefault(value = 20)Pageable pageable){
        MultipartFile file = userInfo.getImage();
        String image = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path");
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userInfo.setAvatar(image);
        userInfo.setDatecreated(new Date());
        userInfo.setRoles(rolesService.getRoleUser());
        userService.saveUser(userInfo);
        Page<Product> listProducts = productService.findAll(pageable);
        return new ModelAndView("home/index", "products", listProducts);
    }
}
