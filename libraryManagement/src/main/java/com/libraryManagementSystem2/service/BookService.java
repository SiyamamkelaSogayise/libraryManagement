package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.repository.BookRepository;
import com.libraryManagementSystem2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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

    public Book getBookByIsbn(String isbn) {
        // Implement logic to fetch book by ISBN from repository
        return bookRepository.findByIsbn(isbn);
    }

    public void updateBook(Book updatedBook) {
        // Implement logic to update book in repository
        bookRepository.save(updatedBook);
    }

    public Book getBookById(int bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }



    public Book borrowBook(Integer bookId, Integer userId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (book != null && user != null && book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1);
            bookRepository.save(book);
            // You might want to add logic to track the borrowing details
            return book;
        }
        return null;
    }


    public Book returnBook(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);

        if (book != null && book.isBorrowed()) {
            book.setBorrowedBy(null);
            book.setBorrowed(false);
            return bookRepository.save(book);
        }
        return null;
    }





    // Add more methods for updating, deleting, and retrieving books as needed
}
