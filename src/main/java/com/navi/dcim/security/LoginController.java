package com.navi.dcim.security;

import com.navi.dcim.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout, Model model) {

        String errorMessage = null;
        if (error != null) {
            model.addAttribute("error", error);
        }

        if (logout != null) {
            model.addAttribute("logout", logout);
        }

        return "login";
    }

}
