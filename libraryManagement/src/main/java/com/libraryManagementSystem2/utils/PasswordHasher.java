package com.libraryManagementSystem2.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword1 = "$2a$10$7.16bnJ0P3MbE.K/GlSK4u5Q7.9MkMiFJ/8Q5pqEoj9NKnZ3bl6FC";  // Replace with your actual admin password
        String rawPassword2 = "$2a$10$rIv7H9Z/pdDxU54XPBz7h.ZP2o7sZ0rQX/Y2tB/BX3KjPaEKs0.yO";  // Replace with your actual admin password

        String hashedPassword1 = encoder.encode(rawPassword1);
        String hashedPassword2 = encoder.encode(rawPassword2);

        System.out.println("Hashed password 1: " + hashedPassword1);
        System.out.println("Hashed password 2: " + hashedPassword2);
    }
}
