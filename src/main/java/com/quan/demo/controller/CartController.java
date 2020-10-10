package com.quan.demo.controller;

import com.quan.demo.models.*;
import com.quan.demo.service.OrdersDetailService;
import com.quan.demo.service.OrdersService;
import com.quan.demo.service.ProductService;
import com.quan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    @ModelAttribute("user")
    private UserInfo getPrincipal() {
        UserInfo userInfo = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userInfo = userService.findOne(((UserDetails) principal).getUsername());
        }
        return userInfo;
    }

    @GetMapping("/buy/{id}")
    public ModelAndView buyProduct(@PathVariable("id") Long id, HttpSession session,
                                   @SortDefault(sort = {"id"}) @PageableDefault(value = 10) Pageable pageable) {
        if (session.getAttribute("cart") == null) {
            List<ItemsCart> carts = new ArrayList<>();
            carts.add(new ItemsCart(productService.findOne(id), 1, productService.findOne(id).getPrice()));
            session.setAttribute("cart", carts);
        } else {
            List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
            int index = isExists(id, carts);
            if (index == -1) {
                carts.add(new ItemsCart(productService.findOne(id), 1, productService.findOne(id).getPrice()));
            } else {
                int quanlity = carts.get(index).getQuantity() + 1;
                carts.get(index).setQuantity(quanlity);
                carts.get(index).setTotal(quanlity * carts.get(index).getProduct().getPrice());
            }
            session.setAttribute("cart", carts);
        }
        Page<Product> products = productService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("user/home");
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/cart")
    public ModelAndView cartView(ModelMap modelMap, HttpSession session) {
        if (session.getAttribute("cart") == null) {
            List<ItemsCart> carts = new ArrayList<>();
            session.setAttribute("cart", carts);
            modelMap.put("total", 0);
        } else {
            modelMap.put("total", sum(session));
        }
        return new ModelAndView("cart/cart");
    }

    @GetMapping("/remove/{id}")
    public ModelAndView removeBuyProduct(@PathVariable("id") Long id, HttpSession session) {
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        int index = isExists(id, carts);
        carts.remove(index);
        session.setAttribute("cart", carts);
        return new ModelAndView("cart/cart");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateBuyProduct(@RequestBody String quality, HttpServletRequest request,
                                                   HttpSession session) {
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        Product product = productService.findByName(quality);
        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).getProduct().equals(product)) {
                carts.get(i).setQuantity((carts.get(i).getQuantity()) + 1);
            }
        }
        String data = "done";
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/clear")
    public ModelAndView clearCart(HttpSession session){
        session.removeAttribute("cart");
        return new ModelAndView("cart/cart");
    }

    @GetMapping("/save")
    public ModelAndView saveCart(HttpSession session){
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        Orders orders = new Orders();
        orders.setAccount_user(getPrincipal().getAccount());
        orders.setDateCreated(new Date());
        ordersService.saveOrders(orders);
        for (ItemsCart cart : carts) {
            Product product = cart.getProduct();
            OrdersDetail orderDetails = new OrdersDetail();
            orderDetails.setIdorder(orders.getId());
            orderDetails.setIdproduct(product.getId());
            orderDetails.setQuantity(cart.getQuantity());
            orderDetails.setPrice(cart.getQuantity() * product.getPrice());
            ordersDetailService.saveOrdersDetail(orderDetails);
        }
        session.removeAttribute("cart");
        return new ModelAndView("user/viewinformation");
    }

    private int isExists(Long id, List<ItemsCart> carts) {
        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).getProduct().getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private double sum(HttpSession session) {
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        double sum = 0;
        for (ItemsCart itemsCart : carts) {
            sum += itemsCart.getQuantity() * itemsCart.getProduct().getPrice();
        }
        return sum;
    }
}
