package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.BookService;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;


    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;

        this.userService = userService;
    }

    @GetMapping("/add")
    public String getAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "dashboard"; // Ensure you have an HTML template named "add_book_form.html"
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.addBook(book.getIsbn(), book.getTitle(), book.getCategory(), book.getQuantity(), book.getAuthor());
        return "redirect:/admin/books/list"; // Redirect to list of books after adding
    }

    @GetMapping("/list")
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "list_books"; // Ensure you have an HTML template named "list_books.html"
    }

    @GetMapping("/list/json")
    @ResponseBody
    public Iterable<Book> listBooksJson() {
        return bookService.getAllBooks();
    }

    @GetMapping("/searchBooks")
    public String searchBooks(@RequestParam("searchQuery") String searchQuery, Model model) {
        List<Book> books = bookService.searchBooks(searchQuery); // Implement search logic in your service
        model.addAttribute("books", books);
        return "list_books"; // Thymeleaf template name
    }

    @GetMapping("/edit/{isbn}")
    public String getEditBookForm(@PathVariable String isbn, Model model) {
        Book book = bookService.getBookByIsbn(isbn); // Implement this method in BookService
        model.addAttribute("book", book);
        return "edit_book_form"; // Ensure you have an HTML template named "edit_book_form.html"
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute Book updatedBook) {
        bookService.updateBook(updatedBook); // Implement this method in BookService
        return "redirect:/admin/books/list"; // Redirect to list of books after editing
    }

    @PostMapping("/{bookId}/borrow")
    public String borrowBook(@PathVariable("bookId") Integer bookId, Principal principal, Model model) {
        String username = principal.getName(); // Get logged-in username
        User user = userService.findByUsername(username);

        if (user != null) {
            Book borrowedBook = bookService.borrowBook(bookId, user.getId()); // Adjust this according to your service method
            if (borrowedBook != null) {
                model.addAttribute("message", "Book borrowed successfully!");
            } else {
                model.addAttribute("message", "Failed to borrow the book. Please try again.");
            }
        } else {
            model.addAttribute("message", "User not found. Please log in again.");
        }

        return "redirect:/user/portal"; // Redirect to the user portal page or another appropriate page
    }



    @PostMapping("/{bookId}/return")
    public ResponseEntity<Book> returnBook(@PathVariable Integer bookId) {
        Book returnedBook = bookService.returnBook(bookId);
        if (returnedBook != null) {
            return ResponseEntity.ok(returnedBook);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }






    // Additional CRUD operations (update, delete, etc.) can be added here
}
