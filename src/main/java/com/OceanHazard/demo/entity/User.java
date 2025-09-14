package com.OceanHazard.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String username;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String password;

    public HashSet<String> roles=new HashSet<>();

    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime updatedAt = LocalDateTime.now();


    public User() {
    }


    public User(Long id, String username, String email, String password, HashSet<String> role,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}
