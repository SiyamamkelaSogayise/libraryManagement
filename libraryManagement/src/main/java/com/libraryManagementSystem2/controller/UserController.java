package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.BookService;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/help")
    public String getHelpPage() {
        return "help_page"; // Return the name of the HTML template for the help page
    }

    @PostMapping("/help")
    public String help_page() {
        return "help_page";
    }

    @GetMapping("/history")
    public String getBookHistory() {
        return "bookHistory"; // Return the name of the HTML template for the book history page
    }

    @PostMapping("/history")
    public String bookHistory() {
        return "bookHistory";
    }

    @GetMapping("/settings")
    public String getSettings(Model model, @AuthenticationPrincipal Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String emailAddress = principal.getName();
        User user = userService.findByEmail(emailAddress);
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/settings")
    public String settings() {
        return "settings";
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
    /*@GetMapping("/userPortal/user")
    public String userPortal(Model model, Principal principal) {
        String userEmail = principal.getName(); // Assuming principal.getName() gives the user's email
        User user = userService.findByEmail(userEmail); // Fetch user object from service based on email
        model.addAttribute("user", user); // Add user object to the model
        return "userPortal"; // Return the Thymeleaf template name
    }*/

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

    // Forgot Password Mappings
    @GetMapping("/forgot-password")
    public String getForgotPasswordForm() {
        return "forgot_password_page"; // Return the name of the HTML template for the forgot password page
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String forgotEmail, @RequestParam long idNumber, RedirectAttributes redirectAttributes) {
        boolean sent = userService.sendPasswordResetEmail(forgotEmail, idNumber);
        if (sent) {
            redirectAttributes.addFlashAttribute("message", "Password reset email sent.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Email and ID number do not match or user not found.");
        }
        return "redirect:/login";
    }

    @PostMapping("/user/settings/profile")
    public String updateProfile(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {

        // Ensure user object is not null and contains necessary fields
        if (user == null || user.getIdNumber() == 0 || user.getUsername() == null || user.getEmailAddress() == null || user.getPhoneNumber() == null) {
            redirectAttributes.addFlashAttribute("message", "Invalid user information provided.");
            return "redirect:/settings";
        }

        // Perform profile update
        boolean success = userService.updateUserProfile(user.getIdNumber(), user.getUsername(), user.getEmailAddress(), user.getPhoneNumber());

        // Handle success or failure
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to update profile. Please try again.");
        }

        return "redirect:/settings";
    }



}
