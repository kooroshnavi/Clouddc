package com.navi.dcim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.temporal.ChronoUnit;

@Controller
public class MvcUserController {

    @GetMapping("/app/welcome")
    public String helloWorld(Model model) {
        model.addAttribute("date", java.time.LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return "index";
    }
}
