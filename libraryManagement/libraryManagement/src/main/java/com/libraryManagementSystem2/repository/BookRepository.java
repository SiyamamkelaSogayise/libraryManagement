package com.libraryManagementSystem2.repository;

import com.libraryManagementSystem2.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Custom query methods can be added here if needed
}
