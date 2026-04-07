package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final UserRepository userRepository;

    public HealthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        // This triggers a DB connection and keeps Supabase active
        long count = userRepository.count();
        return ResponseEntity.ok("Database Active. User count: " + count);
    }
}