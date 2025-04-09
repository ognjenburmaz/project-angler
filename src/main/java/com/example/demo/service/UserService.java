package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    public User save(User user) { return userRepository.save(user); }

    public User update(User user) { return userRepository.save(user); }
}