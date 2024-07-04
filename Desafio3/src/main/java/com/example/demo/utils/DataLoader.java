package com.example.demo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DataLoader {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword1 = passwordEncoder.encode("password1");
        String encodedPassword2 = passwordEncoder.encode("admin");

        System.out.println("Encoded password for user1: " + encodedPassword1);
        System.out.println("Encoded password for user2: " + encodedPassword2);
    }
}
