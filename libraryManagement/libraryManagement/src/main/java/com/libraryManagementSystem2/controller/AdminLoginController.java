package com.libraryManagementSystem2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {

    @GetMapping("/admin-login")
    public String adminPortal() {
        return "admin_portal"; // Return the name of the HTML template for the admin portal
    }

    // Add methods to handle book management, if needed

}
