package com.reglo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reglo.model.User;



public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
}
