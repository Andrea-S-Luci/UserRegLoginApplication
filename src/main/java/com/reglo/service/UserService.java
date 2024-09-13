package com.reglo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reglo.model.User;
import com.reglo.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final int MAX_ATTEMPTS = 5;

    public String registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }
        userRepository.save(user);
        return "Success";
    }

    public boolean loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false; // User not found
        }
        
        if (user.isLocked()) {
            return false; // User is locked out
        }
        
        if (user.getPassword().equals(password)) {
            // Reset login attempts on successful login
            user.setLoginAttempts(0);
            userRepository.save(user);
            return true;
        } else {
            // Increment login attempts on failed login
            int attempts = user.getLoginAttempts() + 1;
            if (attempts >= MAX_ATTEMPTS) {
                user.setLocked(true);
                userRepository.save(user);
                return false;
            }
            user.setLoginAttempts(attempts);
            userRepository.save(user);
            return false;
        }
    }

    public int getRemainingAttempts(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return 0;
        }
        if (user.isLocked()) {
            return 0;
        }
        return MAX_ATTEMPTS - user.getLoginAttempts();
    }
}