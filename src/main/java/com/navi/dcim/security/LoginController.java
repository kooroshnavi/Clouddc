package com.navi.dcim.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout, Model model) {

        String errorMessage = null;
        if (error != null) {
            errorMessage = "Invalid username or password";
        }

        if (logout != null) {
            errorMessage = "You've been logged out.";
        }

        model.addAttribute("errorMessage", errorMessage);

        return "login";

    }
}
