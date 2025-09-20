package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.FilterData;
import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.repository.UserRepository;
import com.OceanHazard.demo.service.AlertService;
import com.OceanHazard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/all")
    public List<User> getAll(){
        return userService.getAllUsers();
    }

    @GetMapping("/id/{myId}")
    public User getUserById(@PathVariable Long myId){
        return userService.getUserById(myId);
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody User user){
        userService.saveUser(user);
    }

    // Check if username exists
    @GetMapping("/exists/{username}")
    public boolean userExists(@PathVariable String username) {
        return userService.userExists(username);
    }


    @PostMapping("/validate")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        User user = userService.validateAndGetUser(loginData.username, loginData.password);
        // Do not return password
        user.password=null;
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Return user info including roles
        return ResponseEntity.ok(user);
    }


}
