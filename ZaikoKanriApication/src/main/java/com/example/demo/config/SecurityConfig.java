package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/auth/login",
            			    "/auth/register",
            			    "/auth/confirm",
            			    "/auth/complete",
            			    "/auth/registerBack",
            			    "/css/**",
            			    "/js/**",
            			    "/images/**"
            			).permitAll()
                
                .requestMatchers("/auth/login.css").permitAll()
                .requestMatchers("/auth/register.css").permitAll()
                .requestMatchers("/auth/confirm.css").permitAll()
                .requestMatchers("/auth/complete.css").permitAll()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/menu/ProductMasterApp", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/login")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}