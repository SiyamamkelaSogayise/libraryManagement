package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Other mappings...

    @GetMapping("/add-book")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add_book"; // Return the name of the HTML template for adding a book
    }

    @PostMapping("/add-book")
    public String addBook(@ModelAttribute("book") Book book) {
        // Handle book addition logic here
        bookService.saveOrUpdateBook(book);
        return "redirect:/admin-portal"; // Redirect to admin portal after adding book
    }
}
