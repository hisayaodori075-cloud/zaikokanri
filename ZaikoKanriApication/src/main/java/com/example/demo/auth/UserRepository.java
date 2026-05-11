package com.example.demo.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<UserEntity, String> {

    UserEntity findByUsername(String username);
}