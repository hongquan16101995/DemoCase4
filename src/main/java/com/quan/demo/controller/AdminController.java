package com.quan.demo.controller;

import com.quan.demo.models.*;
import com.quan.demo.service.*;
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
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ProductService productService;

    @Autowired
    private TypeProductService typeProductService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private Environment environment;

    @ModelAttribute("typeproducts")
    public Iterable<TypeProduct> getListType() {
        return typeProductService.findAll();
    }

    @GetMapping
    public ModelAndView homeViewAdmin(@RequestParam("regex") Optional<String> regex,
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 15) Pageable pageable) {
        Page<Product> products;
        ModelAndView modelAndView = new ModelAndView("admin/home");
        if (regex.isPresent()) {
            products = productService.findAllByName(regex.get(), pageable);
        } else {
            products = productService.findAll(pageable);
        }
        modelAndView.addObject("regex", regex.orElse(""));
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView createProduct() {
        return new ModelAndView("admin/create", "product", new Product());
    }

    @PostMapping("/create")
    public ModelAndView createHome(@ModelAttribute("product") Product product,
                                   @SortDefault(sort = {"id"}) @PageableDefault(value = 15) Pageable pageable) {
        MultipartFile file = product.getImage();
        String image = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path");
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        product.setAvatar(image);
        productService.saveProduct(product);
        Page<Product> products = productService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("admin/home");
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editProduct(@PathVariable("id") Long id) {
        Product product = productService.findOne(id);
        return new ModelAndView("admin/edit", "product", product);
    }

    @PostMapping("/edit/{id}")
    public ModelAndView updateProduct(@ModelAttribute("product") Product product,
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 15) Pageable pageable) {
        Product product1 = productService.findOne(product.getId());
        MultipartFile file = product.getImage();
        String image = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path");
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!Objects.equals(image, "")) {
            product.setAvatar(image);
        } else {
            product.setAvatar(product1.getAvatar());
        }
        productService.saveProduct(product);
        Page<Product> products = productService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("admin/home");
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/createstaff")
    public ModelAndView registrationAdmin() {
        return new ModelAndView("admin/registration", "user", new UserInfo());
    }

    @PostMapping("createstaff")
    public ModelAndView registrationAccount(@ModelAttribute("user") UserInfo userInfo,
                                            @SortDefault(value = {"id"}) @PageableDefault(value = 15) Pageable pageable) {
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
        userInfo.setRoles(rolesService.getRoleStaff());
        userService.saveUser(userInfo);
        Page<Product> listProducts = productService.findAll(pageable);
        return new ModelAndView("admin/home", "products", listProducts);
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable("id") Long id,
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 15) Pageable pageable) {
        productService.deleteProduct(id);
        return new ModelAndView("admin/home", "products", productService.findAll(pageable));
    }

    @GetMapping("/bill")
    public ModelAndView viewBill(@RequestParam("regex") Optional<String> regex,
                                 @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
        Page<Orders> orders;
        ModelAndView modelAndView = new ModelAndView("admin/allbilladmin");
        if (regex.isPresent()) {
            orders = ordersService.findByAccountUser(regex.get(), pageable);
        } else {
            orders = ordersService.findAll(pageable);
        }
        modelAndView.addObject("regex", regex.orElse(""));
        modelAndView.addObject("orders", orders);
        return modelAndView;
    }

    @GetMapping("/billdetail/{id}")
    public ModelAndView viewBillDetail(@PathVariable("id") Long id) {
        Iterable<OrdersDetail> ordersDetails = ordersDetailService.findOrdersDetailById_Order(id);
        return new ModelAndView("admin/billadmin", "ordersdetail", ordersDetails);
    }

    @GetMapping("/listuser")
    public ModelAndView viewListUser(@RequestParam("regex") Optional<String> regex,
                                     @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable){
        Page<UserInfo> userInfos;
        ModelAndView modelAndView = new ModelAndView("admin/viewuser");
        if (regex.isPresent()) {
            userInfos = userService.findAllByName(regex.get(), rolesService.getRoleUser(), pageable);
        } else {
            userInfos = userService.findAllByRoles(rolesService.getRoleUser(), pageable);
        }
        modelAndView.addObject("regex", regex.orElse(""));
        modelAndView.addObject("users", userInfos);
        return modelAndView;
    }

    @GetMapping("/billuser/{name}")
    public ModelAndView viewBillUser(@PathVariable("name") String name,
                                     @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
        Page<Orders> orders = ordersService.findByAccountUser(name, pageable);
        return new ModelAndView("admin/allbilladmin", "orders", orders);
    }
}
