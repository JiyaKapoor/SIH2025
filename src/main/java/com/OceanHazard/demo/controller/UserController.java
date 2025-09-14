package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
