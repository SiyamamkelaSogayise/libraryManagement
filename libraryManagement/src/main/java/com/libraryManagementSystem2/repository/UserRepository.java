package com.libraryManagementSystem2.repository;

import com.libraryManagementSystem2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional <User> findByEmailAddressAndPassword(String emailAddress, String password);
    Optional<User> findById(long idNumber);
    boolean existsByUsername(String username);

    boolean existsByEmailAddress(String emailAddress);
    User findByEmailAddress(String emailAddress);



    User findByUsername(String username);
}
