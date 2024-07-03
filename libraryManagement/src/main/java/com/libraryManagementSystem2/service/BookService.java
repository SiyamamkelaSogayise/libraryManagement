package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.repository.BookRepository;
import com.libraryManagementSystem2.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, EmailService emailService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Book addBook(String isbn, String title, String category, int quantity, String author) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setCategory(category);
        book.setQuantity(quantity);
        book.setAuthor(author);
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        // Implement your search logic here using bookRepository
        // Example: Searching by title or author
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }
    public Book findById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooksBorrowedByUser(User user) {
        return bookRepository.findByBorrowedByAndBorrowedIsTrue(user);
    }


    @Transactional
    public Book updateBook(Book updatedBook) {
        // Perform validations
        if (updatedBook == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        // Validate ID
        Integer updatedBookId = updatedBook.getId();
        if (updatedBookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null.");
        }

        // Check if the book exists in the database
        Optional<Book> existingBookOptional = bookRepository.findById(updatedBookId);
        if (existingBookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book with ID " + updatedBookId + " not found.");
        }

        // Retrieve the existing book
        Book existingBook = existingBookOptional.get();

        // Example logic for updating fields
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setQuantity(updatedBook.getQuantity());

        // Save and return the updated book
        return bookRepository.save(existingBook);
    }

    public Optional<Book> getBookById(Integer bookId) {
        return bookRepository.findById(bookId);
    }

    public void borrowBook(Book book, User user) {
        book.setQuantity(book.getQuantity() - 1); // Decrease quantity
        book.setBorrowed(true); // Mark as borrowed
        book.setBorrowedBy(user); // Set borrower
        book.setBorrowedDate(LocalDate.now()); // Set current date as borrowed date
        book.setDueDate(LocalDate.now().plusDays(14)); // Set due date as 14 days from borrowed date

        bookRepository.save(book); // Save updated book

        // Add logic to save borrowing record if necessary

        emailService.sendBorrowConfirmation(user.getEmailAddress(), book, user.getName()); // Send confirmation email
    }



    public void returnBook(Integer bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            if (book.isBorrowed()) {
                book.setBorrowed(false);
                book.setBorrowedBy(null);
                book.setBorrowedDate(null);
                book.setDueDate(null);
                book.setQuantity(book.getQuantity() + 1); // Increment quantity

                bookRepository.save(book);
            }
        }
    }


    public List<Book> getBorrowedBooks() {
        return bookRepository.findByBorrowedIsTrue();
    }

    public boolean deleteBookByTitle(String title) {
        Optional<Book> optionalBook = bookRepository.findByTitle(title);
        if (optionalBook.isPresent()) {
            bookRepository.delete(optionalBook.get());
            return true;
        } else {
            return false;
        }
    }

    public Book getBookByTitle(String title) {
        return bookRepository.findByTitle(title).orElse(null);
    }




    // Add more methods for updating, deleting, and retrieving books as needed
}
