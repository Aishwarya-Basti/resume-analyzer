package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email and password are required.");
            return "redirect:/register";
        }

        user.setRole("USER");
        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully. Please sign in.");
        return "redirect:/login";
    }
}
