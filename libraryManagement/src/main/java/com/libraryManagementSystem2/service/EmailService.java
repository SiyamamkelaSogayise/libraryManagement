package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.libraryManagementSystem2.model.User;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(User user) {
        String email = user.getEmailAddress();
        String subject = "Password Reset Request";
        String text = "Dear " + user.getName() + ",\n\n" +
                "Your password reset request has been received. Please use the following password to log in:\n\n" +
                "New Password: " + user.getPassword() + "\n\n" +
                "We recommend that you change your password immediately after logging in.\n\n" +
                "Regards,\n" +
                "Library Management System";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendBorrowConfirmation(String toEmailAddress, Book book, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmailAddress);
        message.setSubject("Book Borrowing Confirmation");
        message.setText("Dear " + userName + ",\n\n" +
                "You have successfully borrowed the book: " + book.getTitle() + " by " + book.getAuthor() + ".\n" +
                "Borrowed Date: " + book.getBorrowedDate() + "\n" +
                "Due Date: " + book.getDueDate() + "\n\n" +
                "Please ensure to return the book on its due date to allow others to enjoy it as well.\n" +
                "Also, kindly keep the book in good condition during your possession.\n\n" +
                "Happy reading!\nLibrary Management System");

        mailSender.send(message);
    }
}