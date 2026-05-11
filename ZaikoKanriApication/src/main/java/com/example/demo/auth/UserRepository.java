package com.example.demo.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<UserEntity, String> {

    // ユーザー検索
    UserEntity findByUsername(String username);

    // ユーザー名重複チェック
    boolean existsByUsername(String username);
}