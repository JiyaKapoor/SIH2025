package com.OceanHazard.demo.service;

import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Save a new user with encoded password
    public User saveUser(User user) {
        user.password=passwordEncoder.encode(user.password);
        return userRepository.save(user);
    }

    // Fetch all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Check if username exists
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Validate credentials and return user if valid
    public User validateAndGetUser(String username, String rawPassword) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (!optUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = optUser.get();

        // Check password using PasswordEncoder
        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return user; // <-- you must return the User
    }


    // Boolean version
    public boolean validateCredentials(String username, String rawPassword) {
        return validateAndGetUser(username, rawPassword) != null;
    }
}
