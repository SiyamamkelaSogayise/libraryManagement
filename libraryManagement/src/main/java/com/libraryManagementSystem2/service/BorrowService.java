package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.repository.BookRepository;
import com.libraryManagementSystem2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class BorrowService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public void borrowBook(int bookId, int userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        book.setBorrowed(true);
        book.setBorrowedBy(user);
        book.setBorrowedDate(LocalDate.now());
        book.setDueDate(LocalDate.now().plusWeeks(2));

        bookRepository.save(book);
    }
}
