package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        model.addAttribute("registerRequest", new User());
        return "register_page";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("loginRequest", new User());
        return "login_page";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model,RedirectAttributes redirectAttributes) {
        System.out.println("Register request: " + user);

        // Validate user input
        if (!isUserInputValid(user, model)) {
            return "register_page"; // Return to registration page with errors
        }

        // Attempt to register new user
        User registeredUser = userService.registerNewUser(user.getName(), user.getIdNumber(), user.getDateOfBirth(), user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword());

        if (registeredUser == null) {
            return "error_page"; // Handle registration failure
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully registered. Please log in.");
            return "redirect:/login"; // Redirect to login page on successful registration
        }
    }

    // Method to validate user input
    private boolean isUserInputValid(User user, Model model) {
        // Add your validation logic here
        boolean isValid = true;

        // Example validation: Check if passwords match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordMatchError", "Passwords do not match");
            isValid = false;
        }

        // Additional validation can be added for other fields

        return isValid;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        System.out.println("Login request: " + user.getEmailAddress());

        // Validate input (optional based on your needs)
        if (user.getEmailAddress() == null || user.getPassword() == null) {
            return "index"; // Return to index page or handle error
        }

        // Authenticate using email and password
        User authenticatedUser = userService.authenticate(user.getEmailAddress(), user.getPassword());


        if (authenticatedUser == null) {
            return "index"; // Handle login failure
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully logged in.");
            return "redirect:/books"; // Redirect to books page on successful login
        }
    }

    @GetMapping("/books")
    public String getBooksPage() {
        return "books"; // Return the name of the HTML template for the books page
    }
}
