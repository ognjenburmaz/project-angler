package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }
    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }
    public User save(User user) { return userRepository.save(user); }
    public void update(User user) { userRepository.save(user); }
    public List<User> allUsers() { return userRepository.findAll(); }

    public int calculateUserLevel(int totalCatches) {
        if (totalCatches >= 100) return 5;
        if (totalCatches >= 50) return 4;
        if (totalCatches >= 25) return 3;
        if (totalCatches >= 10) return 2;
        return 1;
    }

    public String calculateUserTitle(int totalCatches) {
        int level = calculateUserLevel(totalCatches);
        return switch (level) {
            case 5 -> "Master Baiter";
            case 4 -> "Monster Hunter";
            case 3 -> "Pro-fish-ent";
            case 2 -> "Greenhorn";
            default -> "Worm";
        };
    }
}