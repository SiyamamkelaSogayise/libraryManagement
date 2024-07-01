package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Book;
import com.libraryManagementSystem2.repository.BookRepository;
import com.libraryManagementSystem2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import com.libraryManagementSystem2.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("UserService")
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, BookRepository bookRepository, EmailService emailService){
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.emailService = emailService;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }



    // Method for handling forgot password functionality
    public User findByEmail(String email) {
        return userRepository.findByEmailAddress(email);
    }

    public boolean sendPasswordResetEmail(String email, long idNumber) {
        User user = findByEmail(email);
        if (user != null && Long.valueOf(idNumber).equals(user.getIdNumber())) {
            // Send the password reset email
            emailService.sendPasswordResetEmail(user);
            return true;
        }
        return false;
    }
    public User registerNewUser(String name, long idNumber, LocalDate dateOfBirth, String address, String phoneNumber, String emailAddress, String username, String password, String confirmPassword, String role) {
        // Validate input parameters
        if (username == null || password == null || confirmPassword == null ||
                name == null || address == null || phoneNumber == null || emailAddress == null || dateOfBirth == null) {
            return null; // Return null if any required fields are null
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return null; // Return null if passwords do not match
        }

        // Check if a user with the same username or email already exists
        if (userRepository.existsByUsername(username) || userRepository.existsByEmailAddress(emailAddress)) {
            return null; // Return null if username or email already exists
        }

        // Create a new user object and save to repository
        User user = new User();
        user.setName(name);
        user.setIdNumber(idNumber);
        user.setDateOfBirth(dateOfBirth);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setEmailAddress(emailAddress);
        user.setUsername(username);
        user.setPassword(password); // Note: For production, consider hashing the password
        user.setConfirmPassword(confirmPassword);
        user.setRole(role);


        // Set admin role based on specific conditions (example)


        try {
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle any unexpected exceptions during save
        }
    }




public User authenticate(String emailAddress, String password) {
    return userRepository.findByEmailAddressAndPassword(emailAddress, password).orElse(null);
}
    public User addUser(String name, long idNumber, LocalDate dateOfBirth, String address, String phoneNumber,
                        String emailAddress, String username, String password, String confirmPassword, String role) {
        // Validate input parameters
        if (username == null || password == null || confirmPassword == null ||
                name == null || address == null || phoneNumber == null || emailAddress == null || dateOfBirth == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Check if a user with the same username or email already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (userRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("Email address already exists.");
        }

        // Create a new user object
        User user = new User();
        user.setName(name);
        user.setIdNumber(idNumber);
        user.setDateOfBirth(dateOfBirth);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setEmailAddress(emailAddress);
        user.setUsername(username);
        user.setPassword(password); // Note: For production, consider hashing the password
        user.setRole(role);

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add user.", e);
        }
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getCurrentUser() {
        // Logic to retrieve the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }



    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean updateUserProfile(long idNumber, String username, String email, String phone) {
        Optional<User> optionalUser = userRepository.findById(idNumber);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(username);
            user.setEmailAddress(email);
            user.setPhoneNumber(phone);
            userRepository.save(user);
            return true;
        } else {
            return false; // User with userId not found
        }
    }
}
