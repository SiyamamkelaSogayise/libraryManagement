package com.libraryManagementSystem2.controller;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.service.BookService;
import com.libraryManagementSystem2.service.EmailService;
import com.libraryManagementSystem2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;
    private final EmailService emailService;


    @Autowired
    public BookController(BookService bookService, UserService userService, EmailService emailService) {
        this.bookService = bookService;

        this.userService = userService;
        this.emailService = emailService;
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

    @GetMapping("/edit/{title}")
    public String getEditBookForm(@PathVariable String title, Model model) {
        Book book = bookService.getBookByTitle(title); // Use title instead of ISBN
        model.addAttribute("book", book);
        return "edit_book_form"; // Ensure you have an HTML template named "edit_book_form.html"
    }


    @GetMapping("/{bookId}/borrow")
    public String showBorrowBookPage(@PathVariable Integer bookId, Model model) {
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "borrow";
        } else {
            return "redirect:/admin/books/list"; // Redirect if the book does not exist
        }
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam Integer bookId, @RequestParam String emailAddress, @RequestParam String name, RedirectAttributes redirectAttributes) {
        Optional<Book> bookOptional = bookService.getBookById(bookId);
        Optional<User> userOptional = userService.getUserByEmailAddressWithSomeParameters(emailAddress, name);

        if (bookOptional.isPresent() && userOptional.isPresent()) {
            Book book = bookOptional.get();
            User user = userOptional.get();

            if (book.getQuantity() > 0) {
                // Set borrowedBy and borrowedDate
                book.setBorrowedBy(user);
                book.setBorrowed(true);
                book.setBorrowedDate(LocalDate.now()); // Capture current date as borrowedDate
                // Calculate dueDate (borrowedDate + 14 days)
                LocalDate dueDate = LocalDate.now().plusDays(14);
                book.setDueDate(dueDate);

                bookService.borrowBook(book, user);

                // Send confirmation email to user
                emailService.sendBorrowConfirmation(user.getEmailAddress(), book, user.getName());

                redirectAttributes.addFlashAttribute("message", "Book borrowed successfully!");
                return "redirect:/userPortal";
            } else {
                redirectAttributes.addFlashAttribute("message", "Book is currently unavailable.");
                return "redirect:/admin/books/" + bookId + "/borrow";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "User email does not exist.");
            return "redirect:/admin/books/" + bookId + "/borrow";
        }
    }

    @GetMapping("/borrowedBooks")
    public String listBorrowedBooks(Model model) {
        List<Book> borrowedBooks = bookService.getBorrowedBooks();
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "issuedBooks"; // Create an HTML template named "borrowed_books.html"
    }


    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable Integer bookId, RedirectAttributes redirectAttributes) {
        Optional<Book> bookOptional = bookService.getBookById(bookId);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            if (book.isBorrowed()) {
                // Perform return operations
                bookService.returnBook(bookId);

                // Additional operations (e.g., sending return confirmation email)
                // emailService.sendReturnConfirmation(book.getBorrowedBy().getEmailAddress(), book);

                redirectAttributes.addFlashAttribute("message", "Book returned successfully!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Book is not currently borrowed.");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Book not found.");
        }

        return "redirect:/admin/books/borrowBooks";
    }


    @PostMapping("/update")
    public String updateBook(@ModelAttribute("book") Book book, Model model) {
        Book updatedBook = bookService.updateBook(book);
        if (updatedBook == null) {
            model.addAttribute("error", "Book not found or invalid update.");
            return "edit_book_form";
        }
        return "redirect:/admin/books/list";
    }

    @PostMapping("/delete/{title}")
    public String deleteBook(@PathVariable String title, RedirectAttributes redirectAttributes) {
        boolean isDeleted = bookService.deleteBookByTitle(title);
        if (!isDeleted) {
            redirectAttributes.addFlashAttribute("error", "Book not found or could not be deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully.");
        }
        return "redirect:/admin/books/list";
    }

    @GetMapping("/history")
    public String getBorrowedBooks(Model model, Principal principal) {
        String emailAddress = principal.getName();
        User user = userService.findByEmail(emailAddress);
        List<Book> borrowedBooks = bookService.getBooksBorrowedByUser(user.getId());
        model.addAttribute("books", borrowedBooks);
        return "BookHistory"; // Ensure you have an HTML template named "history.html"
    }


}
