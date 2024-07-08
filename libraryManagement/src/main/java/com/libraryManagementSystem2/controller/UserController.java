package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.BookService;
import com.libraryManagementSystem2.service.EmailService;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.validation.BindingResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Controller
public class UserController {

    private final UserService userService;
    private final BookService bookService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, BookService bookService, EmailService emailService) {
        this.userService = userService;

        this.bookService = bookService;
        this.emailService = emailService;
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





    @PostMapping("/history")
    public String getBookHistory() {
        return "bookHistory"; // Return the name of the HTML template for the book history page
    }

    @GetMapping("/settings")
    public String getSettings() {
        return "settings"; // Return the name of the HTML template for the settings page
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
        User registeredUser = userService.registerNewUser(user.getName(), user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword(), role);

        if (registeredUser == null) {
            return "error_page"; // Handle registration failure
        } else {
            // Example list of books (replace with your actual logic to get books)
            List<Book> books = bookService.getAllBooks();

            // Add books to model attribute
            model.addAttribute("books", books);


            redirectAttributes.addFlashAttribute("message", "Successfully registered, you may check your emails for Library card.");
            return "redirect:/userPortal";
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
    @ResponseBody
    public Map<String, Object> handleForgotPassword(@RequestParam String forgotEmail, @RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        boolean sent = userService.sendPasswordResetEmail(forgotEmail, username);
        response.put("sent", sent);
        if (sent) {
            response.put("message", "Password reset email sent.");
        } else {
            response.put("message", "Email and username do not match or user not found.");
        }
        return response; // Return JSON response
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "manage_users"; // Ensure you have an HTML template named "manage_users.html"
    }


    @PostMapping("/admin/users/add")
    public String addUser(@ModelAttribute User user, Model model,@RequestParam("role")String role, RedirectAttributes redirectAttributes) {
        // Add validation if needed
        User newUser = userService.registerNewUser(user.getName(),user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword(), role);
        if (newUser == null) {
            // Handle registration failure
            return "error_page";
        } else {
            redirectAttributes.addFlashAttribute("message", "User added successfully.");
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/updateUser")
    public String updateBook(@ModelAttribute("user") User user, Model model) {
        User updatedUser = userService.updateUser(user);
        if (updatedUser == null) {
                model.addAttribute("error", "User not found or invalid update.");
            return "edit_user_form";
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "edit_user_form";
    }

    // Add this method to your UserController
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = userService.deleteUserById(id);
        if (!isDeleted) {
            redirectAttributes.addFlashAttribute("error", "User not found or could not be deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
        }
        return "redirect:/admin/users";
    }


    @DeleteMapping("/admin/users/delete/{id}")
    @ResponseBody
    public Map<String, String> deleteUser(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();
        boolean isDeleted = userService.deleteUserById(id);
        if (isDeleted) {
            response.put("message", "User deleted successfully.");
        } else {
            response.put("message", "User not found or could not be deleted.");
        }
        return response;
    }
}