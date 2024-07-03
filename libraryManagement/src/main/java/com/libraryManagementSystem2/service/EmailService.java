package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


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

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Handle exception (e.g., log the error, notify admin)
            e.printStackTrace();
        }
    }

    public void sendLibraryCard(User user) {
        String email = user.getEmailAddress();
        String subject = "Your Library Card";

        // Generate virtual library card as image
        byte[] cardImageBytes = generateLibraryCardImage(user);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText("Dear " + user.getName() + ",\n\n" +
                    "Welcome to our library! Here is your library card.\n\n" +
                    "Please find attached your virtual library card.");

            DataSource dataSource = new ByteArrayDataSource(cardImageBytes, "image/jpeg");
            helper.addAttachment("LibraryCard.jpg", dataSource);

            System.out.println("Sending email to " + email);  // Debug statement
            mailSender.send(message);
            System.out.println("Email sent successfully!");  // Debug statement
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());  // Debug statement
            // Handle exception
        }

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

    private byte[] generateLibraryCardImage(User user) {
        // Create a blank image
        BufferedImage image = new BufferedImage(400, 250, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background color
        g2d.setColor(new Color(240, 240, 240));  // Light gray background
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        // Draw card outline
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(10, 10, image.getWidth() - 20, image.getHeight() - 20);

        // Add library card title
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Library Card", 140, 40);

        // Add user details
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Name: " + user.getName(), 40, 80);
        g2d.drawString("ID: " + user.getIdNumber(), 40, 100);
        g2d.drawString("Library Card Number: " + user.getId(), 40, 120);

        // Simulate a barcode
        g2d.fillRect(40, 140, 180, 50);  // Barcode placeholder

        // Add issuer details
        g2d.setFont(new Font("Arial", Font.ITALIC, 12));
        g2d.drawString("Issued by: Library Management System", 40, 200);


        // Add issuer details
        g2d.setFont(new Font("Arial", Font.ITALIC, 12));
        g2d.drawString("Issued by: Library Management System", 40, 200);

        g2d.dispose();

        // Convert image to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return byteArrayOutputStream.toByteArray();
    }


}









