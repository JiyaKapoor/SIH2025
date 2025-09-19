package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.FilterData;
import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.service.AlertService;
import com.OceanHazard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Validate credentials (username + password)
    @PostMapping("/validate")
    public boolean validateUser(@RequestParam String username, @RequestParam String password) {
        return userService.validateCredentials(username, password);
    }
}
