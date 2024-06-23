// AdminService.java
package com.libraryManagementSystem2.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    // Simulated admin details (replace with database or secure storage)
    private final String adminUsername = "admin";
    private final String adminPassword = "$2a$10$OwMF7i6KsBh3htf.Zil5jukzYD2TEvJhEw4cq4xYnQ71ivI6Mbc3O"; // Example hash for 'adminpassword'

    public boolean validateCredentials(String username, String password) {
        return adminUsername.equals(username) && BCrypt.checkpw(password, adminPassword);
    }
}
