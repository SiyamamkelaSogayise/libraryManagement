package com.libraryManagementSystem2.service;

import com.libraryManagementSystem2.model.Admin;
import com.libraryManagementSystem2.model.User;
import com.libraryManagementSystem2.repository.AdminRepository;
import com.libraryManagementSystem2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminService(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    public Admin authenticate(String username, String password) {
        Admin admin = adminRepository.findAdminByUsername(username);
        if (admin != null && admin.checkPassword(password)) {
            return admin;
        }
        return null;
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
