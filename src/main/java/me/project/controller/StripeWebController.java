package me.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StripeWebController {

    @RequestMapping("/api/v1/stripe")
    public String home(Model model) {
        return "index";
    }
}
