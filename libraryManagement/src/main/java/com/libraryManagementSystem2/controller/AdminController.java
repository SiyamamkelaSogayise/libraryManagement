package com.libraryManagementSystem2.controller;


import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller

public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/register")
    public String getAdminRegisterForm(Model model) {
        model.addAttribute("registerRequest", new User());
        return "registration_page"; // Reuse the same registration page
    }

    @PostMapping("/admin/register")
    public String registerAdmin(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("Admin Register request: " + user);

        // Validate user input
        if (!isUserInputValid(user, model)) {
            return "registration_page"; // Return to registration page with errors
        }

        // Attempt to register new admin
        User registeredAdmin = userService.registerNewUser(user.getName(), user.getIdNumber(), user.getDateOfBirth(), user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword(), "ADMIN");

        if (registeredAdmin == null) {
            return "error_page"; // Handle registration failure
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully registered as admin. Please log in.");
            return "redirect:/login"; // Redirect to login page on successful registration
        }
    }


    private boolean isUserInputValid(User user, Model model) {
        boolean isValid = true;

        // Example validation: Check if passwords match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordMatchError", "Passwords do not match");
            isValid = false;
        }

        // Additional validation can be added for other fields

        return isValid;
    }


    @GetMapping("users/add")
    public String showAddUserForm(Model model) {
        // Prepare model attributes if needed for form population
        return "manager_users"; // Assuming "add_user.html" Thymeleaf template exists
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String name,
                          @RequestParam long idNumber,
                          @RequestParam LocalDate dateOfBirth,
                          @RequestParam String address,
                          @RequestParam String phoneNumber,
                          @RequestParam String emailAddress,
                          @RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String role,
                          Model model) {
        // Convert dateOfBirth from String to LocalDate


        // Call UserService to add a new user
        User newUser = userService.addUser(name, idNumber, dateOfBirth, address, phoneNumber, emailAddress,
                username, password, confirmPassword, role);

        if (newUser != null) {
            // Redirect to list of users or another appropriate page
            return "redirect:/admin/users/list";
        } else {
            // Handle error scenario, for example:
            model.addAttribute("error", "Failed to add user.");
            return "managers"; // Return to add_user.html with error message
        }
    }
}
