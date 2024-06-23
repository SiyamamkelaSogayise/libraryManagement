package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.repository.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.libraryManagementSystem2.model.User;

import java.time.LocalDate;
import java.util.Optional;

@Service("UserService")
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }



    public User registerNewUser(String name, long idNumber, LocalDate dateOfBirth, String address, String phoneNumber, String emailAddress, String username, String password, String confirmPassword) {
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

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle any unexpected exceptions during save
        }
    }



    public User authenticate(String emailAddress, String password){
        return userRepository.findByEmailAddressAndPassword(emailAddress, password).orElse(null);
    }




}
