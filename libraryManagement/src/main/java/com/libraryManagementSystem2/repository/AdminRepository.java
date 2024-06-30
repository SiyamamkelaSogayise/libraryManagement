package com.libraryManagementSystem2.repository;

import com.libraryManagementSystem2.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findAdminByUsername(String username);
}
