package com.quan.demo.controller;

import com.quan.demo.models.Product;
import com.quan.demo.models.TypeProduct;
import com.quan.demo.models.UserInfo;
import com.quan.demo.service.ProductService;
import com.quan.demo.service.TypeProductService;
import com.quan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ProductService productService;

    @Autowired
    private TypeProductService typeProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @ModelAttribute("user")
    private UserInfo getPrincipal() {
        UserInfo userInfo = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userInfo = userService.findOne(((UserDetails) principal).getUsername());
        }
        return userInfo;
    }

    @ModelAttribute("typeproducts")
    public Iterable<TypeProduct> getListType() {
        return typeProductService.findAll();
    }

    @GetMapping
    public ModelAndView homeViewUser(@RequestParam("regex") Optional<String> regex,
                                     @SortDefault(sort = {"id"}) @PageableDefault(value = 20) Pageable pageable) {
        Page<Product> products;
        ModelAndView modelAndView = new ModelAndView("user/home");
        if (regex.isPresent()) {
            products = productService.findAllByName(regex.get(), pageable);
        } else {
            products = productService.findAll(pageable);
        }
        modelAndView.addObject("products", products);
        modelAndView.addObject("regex", regex.orElse(""));
        return modelAndView;
    }

    @GetMapping("/view")
    public ModelAndView viewInformation() {
        return new ModelAndView("user/viewinformation");
    }

    @GetMapping("/information")
    public ModelAndView informationUser() {
        return new ModelAndView("user/information");
    }

    @PostMapping("/information")
    public ModelAndView updateInformationUser(@ModelAttribute("user") UserInfo userInfo) {
        MultipartFile file = userInfo.getImage();
        String image = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path");
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!Objects.equals(image, "")){
            userInfo.setAvatar(image);
        }
        userService.saveUser(userInfo);
        return new ModelAndView("user/viewinformation");
    }

    @GetMapping("/password")
    public ModelAndView passwordUser() {
        return new ModelAndView("user/password");
    }

    @PostMapping("/password")
    public ModelAndView updatePasswordUser(@ModelAttribute("user") UserInfo userInfo,
                                           @RequestParam("newpass") String newpass, @RequestParam("pass") String pass) {
        if (!pass.equals(userInfo.getPassword())) {
            String message = "Sai mật khẩu";
            return new ModelAndView("user/password", "message", message);
        } else
            userInfo.setPassword(newpass);
        userService.saveUser(userInfo);
        return new ModelAndView("user/viewinformation");
    }

    @GetMapping("/viewproduct/{id}")
    public ModelAndView viewProduct(@PathVariable("id") Long id) {
        Product product = productService.findOne(id);
        return new ModelAndView("user/viewproduct", "product", product);
    }
}
