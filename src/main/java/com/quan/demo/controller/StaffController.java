package com.quan.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/staff")
public class StaffController {
    @GetMapping
    public ModelAndView homeStaff(){
        return new ModelAndView("staff/home");
    }
}
