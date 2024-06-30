package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.BookService;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class UserController {

    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;

        this.bookService = bookService;
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
    public String register(@ModelAttribute User user, @RequestParam("role") String role, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("Register request: " + user);

        // Validate user input
        if (!isUserInputValid(user, model)) {
            return "register_page"; // Return to registration page with errors
        }

        // Attempt to register new user
        User registeredUser = userService.registerNewUser(user.getName(), user.getIdNumber(), user.getDateOfBirth(), user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword(), role);

        if (registeredUser == null) {
            return "error_page"; // Handle registration failure
        } else {
            // Example list of books (replace with your actual logic to get books)
            List<Book> books = bookService.getAllBooks();

            // Add books to model attribute
            model.addAttribute("books", books);

            redirectAttributes.addFlashAttribute("message", "Successfully registered. Please log in.");
            return "redirect:/login";
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
            return "login_page"; // Return to login page or handle error
        }

        // Authenticate using email and password
        User authenticatedUser = userService.authenticate(user.getEmailAddress(), user.getPassword());

        if (authenticatedUser == null) {
            return "login_page"; // Handle login failure
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully logged in.");

            if (authenticatedUser.isAdmin()) {
                return "redirect:/admin/dashboard"; // Redirect to admin dashboard if user is admin
            } else {
                return "redirect:/userPortal"; // Redirect to books page for regular users
            }
        }
    }

    @GetMapping("/books")
    public String getBooksPage() {
        return "userPortal"; // Return the name of the HTML template for the books page
    }
    @GetMapping("/logout")
    public String getLogOut(){
        return "index";
    }
    @GetMapping("/admin/dashboard")
    public String getAdminDashboard() {
        return "dashboard"; // Return the name of your admin dashboard HTML template
    }
    @GetMapping("/userPortal")
    public String userPortal(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "userPortal";
    }
    @GetMapping("/userPortal/user")
    public String userPortal(Model model, Principal principal) {
        String userEmail = principal.getName(); // Assuming principal.getName() gives the user's email
        User user = userService.findByEmail(userEmail); // Fetch user object from service based on email
        model.addAttribute("user", user); // Add user object to the model
        return "userPortal"; // Return the Thymeleaf template name
    }

    @GetMapping("/admin/dashboard/managers")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "manage_users"; // Ensure you have an HTML template named "manage_users.html"
    }

    @GetMapping("/currentUser")
    public String currentUser(Model model) {
        // Assuming you have a way to get the currently logged-in user
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);

        // Add books to the model as well
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        return "userPortal";
    }




}
