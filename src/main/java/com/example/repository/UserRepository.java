package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
//    List<User> findByEmailEndingWith(String domain);
//    List<User> findByFirstNameContainingIgnoreCase(String firstName);
//    List<User> findByLastNameContainingIgnoreCase(String lastName);
//    List<User> findByJobContainingIgnoreCase(String job);
//    List<User> findByIdContaining(Long id);

    Optional<User> findById(Long id);
}
