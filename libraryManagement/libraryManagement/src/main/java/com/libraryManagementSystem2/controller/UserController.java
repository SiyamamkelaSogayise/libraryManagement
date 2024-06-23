package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.service.UserService;
import com.libraryManagementSystem2.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("Register request: " + user);

        if (!isUserInputValid(user, model)) {
            return "register_page";
        }

        User registeredUser = userService.registerNewUser(user.getName(), user.getIdNumber(), user.getDateOfBirth(), user.getAddress(), user.getPhoneNumber(), user.getEmailAddress(), user.getUsername(), user.getPassword(), user.getConfirmPassword());

        if (registeredUser == null) {
            return "error_page";
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully registered. Please log in.");
            return "redirect:/login";
        }
    }

    private boolean isUserInputValid(User user, Model model) {
        boolean isValid = true;

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordMatchError", "Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        System.out.println("Login request: " + user.getEmailAddress());

        if (user.getEmailAddress() == null || user.getPassword() == null) {
            return "index";
        }

        User authenticatedUser = userService.authenticate(user.getEmailAddress(), user.getPassword());

        if (authenticatedUser == null) {
            return "index";
        } else {
            redirectAttributes.addFlashAttribute("message", "Successfully logged in.");
            return "redirect:/books";
        }
    }

    @GetMapping("/books")
    public String getBooksPage() {
        return "books";
    }

    // Admin Routes
    @GetMapping("/admin/login")
    public String getAdminLoginForm(Model model) {
        model.addAttribute("loginRequest", new User());
        return "admin_login";
    }



    @GetMapping("/admin/portal")
    public String getAdminPortal(Model model) {
        model.addAttribute("book", new Book());
        return "admin_portal";
    }

    @PostMapping("/admin/add-book")
    public String addBook(@ModelAttribute("book") Book book) {
        bookService.saveOrUpdateBook(book);
        return "redirect:/admin/portal";
    }
}
