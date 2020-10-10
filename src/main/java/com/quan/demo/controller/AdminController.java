package com.quan.demo.controller;

import com.quan.demo.models.Orders;
import com.quan.demo.models.OrdersDetail;
import com.quan.demo.models.Product;
import com.quan.demo.models.TypeProduct;
import com.quan.demo.service.OrdersDetailService;
import com.quan.demo.service.OrdersService;
import com.quan.demo.service.ProductService;
import com.quan.demo.service.TypeProductService;
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
    private Environment environment;

    @ModelAttribute("typeproducts")
    public Iterable<TypeProduct> getListType() {
        return typeProductService.findAll();
    }

    @GetMapping
    public ModelAndView homeViewAdmin(@RequestParam("regex") Optional<String> regex,
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 20) Pageable pageable) {
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
                                   @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
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
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
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
        }else {
            product.setAvatar(product1.getAvatar());
        }
        productService.saveProduct(product);
        Page<Product> products = productService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("admin/home");
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable("id") Long id,
                                      @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
        productService.deleteProduct(id);
        return new ModelAndView("admin/home", "products", productService.findAll(pageable));
    }

    @GetMapping("/bill")
    public ModelAndView viewBill(@SortDefault(sort = {"id"}) @PageableDefault(value = 5) Pageable pageable){
        Page<Orders> orders = ordersService.findAll(pageable);
        return new ModelAndView("admin/allbilladmin", "orders", orders);
    }

    @GetMapping("/billdetail/{id}")
    public ModelAndView viewBillDetail(@PathVariable("id") Long id){
        Iterable<OrdersDetail> ordersDetails = ordersDetailService.findOrdersDetailById_Order(id);
        return new ModelAndView("admin/billadmin", "ordersdetail", ordersDetails);
    }
}
