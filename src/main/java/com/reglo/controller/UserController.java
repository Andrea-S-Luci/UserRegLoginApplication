package com.reglo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reglo.model.User;
import com.reglo.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String mainPage(Model model) {
        return "main";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        String registrationResult = userService.registerUser(user);
        if ("Email already exists".equals(registrationResult)) {
            model.addAttribute("error", user.getEmail()+" is already registered.");
            return "register";
        }
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "success", required = false) String success, 
                                @RequestParam(value = "error", required = false) String error, 
                                @RequestParam(value = "email", required = false) String email,
                                Model model) {
        if (success != null) {
            model.addAttribute("message", "Registration successful, please login");
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (email != null) {
            int remainingAttempts = userService.getRemainingAttempts(email);
            model.addAttribute("remainingAttempts", remainingAttempts);
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        boolean loginSuccess = userService.loginUser(email, password);
        if (loginSuccess) {
            return "hello";
        } else {
            int remainingAttempts = userService.getRemainingAttempts(email);
            if (remainingAttempts == 0) {
                model.addAttribute("error", "You have been locked out due to too many failed attempts.");
            } else {
                model.addAttribute("error", "Invalid email or password. Attempts remaining: " + remainingAttempts);
            }
            return "login";
        }
    }
}