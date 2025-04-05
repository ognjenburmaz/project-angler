package com.example.demo.controller;

import com.example.demo.model.CaughtFish;
import com.example.demo.model.User;
import com.example.demo.service.CaughtFishService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/users")
@Controller
public class UserController {
    private final UserService userService;
    private final CaughtFishService caughtFishService;
    public UserController(UserService userService, CaughtFishService caughtFishService) {
        this.userService = userService;
        this.caughtFishService = caughtFishService;
    }

    @GetMapping("/me")
    public String authenticatedUser(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        model.addAttribute("user", user);

        model.addAttribute("joinDate", user.getJoinDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        int level = 1;
        if (user.getTotalCatches() >= 100) { level = 5; }
        else if (user.getTotalCatches() >= 50) { level = 4; }
        else if (user.getTotalCatches() >= 25) { level = 3; }
        else if (user.getTotalCatches() >= 10) { level = 2; }
        model.addAttribute("level", level);

        String title = "Worm";
        if (level == 5) { title = "Master Baiter"; }
        else if (level == 4) { title = "Monster Hunter"; }
        else if (level == 3) { title = "Pro-fish-ent"; }
        else if (level == 2) { title = "Greenhorn"; }
        model.addAttribute("title", title);

        CaughtFish fish = caughtFishService.getLastCaughtFishByOwnerId(user);
        model.addAttribute("fish", fish);

        model.addAttribute("date", fish.getTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

        return "userProfile";
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}