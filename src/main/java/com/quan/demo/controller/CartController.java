package com.quan.demo.controller;

import com.quan.demo.models.*;
import com.quan.demo.service.OrdersDetailService;
import com.quan.demo.service.OrdersService;
import com.quan.demo.service.ProductService;
import com.quan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin("*")
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

    @PostMapping("/buy")
    public ResponseEntity<String> buyAjax(@RequestBody Product product, HttpSession session){
        if (session.getAttribute("cart") == null) {
            List<ItemsCart> carts = new ArrayList<>();
            carts.add(new ItemsCart(productService.findOne(product.getId()), 1));
            session.setAttribute("cart", carts);
        } else {
            List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
            int index = isExists(product.getId(), carts);
            if (index == -1) {
                carts.add(new ItemsCart(productService.findOne(product.getId()), 1));
            } else {
                int quanlity = carts.get(index).getQuantity() + 1;
                carts.get(index).setQuantity(quanlity);
            }
            session.setAttribute("cart", carts);
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ModelAndView removeBuyProduct(@PathVariable("id") Long id, HttpSession session, ModelMap modelMap) {
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        int index = isExists(id, carts);
        carts.remove(index);
        modelMap.put("total", sum(session));
        session.setAttribute("cart", carts);
        return new ModelAndView("cart/cart");
    }

//    @GetMapping("/update/{id}")
//    public ModelAndView updateBuyProduct(@PathVariable("id") Long id, @RequestParam("name") String name,
//                                         HttpSession session, ModelMap modelMap) {
//        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
//        Product product = productService.findOne(id);
//        for (ItemsCart cart : carts) {
//            if (cart.getProduct().equals(product)) {
//                if (name.equals("down") && cart.getQuantity() >= 1) {
//                    cart.setQuantity((cart.getQuantity()) - 1);
//                } else if (name.equals("up")) {
//                    cart.setQuantity((cart.getQuantity()) + 1);
//                } else {
//                    cart.setQuantity((cart.getQuantity()));
//                }
//            }
//        }
//        modelMap.put("total", sum(session));
//        session.setAttribute("cart", carts);
//        return new ModelAndView("cart/cart");
//    }

    @PostMapping("/update")
    public ResponseEntity<Ajax> update(@RequestBody Product product, HttpSession session){
        Ajax ajax = new Ajax();
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        Product product1 = productService.findOne(product.getId());
        for (ItemsCart cart : carts) {
            if (cart.getProduct().equals(product1)) {
                if (product.getName().equals("down") && cart.getQuantity() >= 1) {
                    cart.setQuantity((cart.getQuantity()) - 1);
                    ajax.setQuality(cart.getQuantity());
                    ajax.setPrice(cart.getProduct().getPrice()*cart.getQuantity());
                } else if (product.getName().equals("up")) {
                    cart.setQuantity((cart.getQuantity()) + 1);
                    ajax.setQuality(cart.getQuantity());
                    ajax.setPrice(cart.getProduct().getPrice()*cart.getQuantity());
                } else {
                    cart.setQuantity((cart.getQuantity()));
                    ajax.setQuality(cart.getQuantity());
                    ajax.setPrice(cart.getProduct().getPrice()*cart.getQuantity());
                }
            }
        }
        ajax.setTotalPrice(sum(session));
        session.setAttribute("cart", carts);
        return new ResponseEntity<>(ajax, HttpStatus.OK);
    }

    @GetMapping("/clear")
    public ModelAndView clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return new ModelAndView("cart/cart");
    }

    @GetMapping("/save")
    public ModelAndView saveCart(HttpSession session) {
        List<ItemsCart> carts = (List<ItemsCart>) session.getAttribute("cart");
        Orders orders = new Orders();
        orders.setAccountuser(getPrincipal().getAccount());
        orders.setDateCreated(new Date());
        orders.setTotalPrice(sum(session));
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
        return new ModelAndView("user/information");
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
