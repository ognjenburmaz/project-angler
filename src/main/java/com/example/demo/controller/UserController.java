package com.example.demo.controller;

import com.example.demo.dto.CatchResponse;
import com.example.demo.model.User;
import com.example.demo.service.CatchService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CatchService catchService;

    @GetMapping("/me")
    public String profile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String title = userService.calculateUserTitle(user.getTotalCatches());
        int level = userService.calculateUserLevel(user.getTotalCatches());
        CatchResponse lastCatch = catchService.getLastUserCatch(user);

        model.addAttribute("user", user);
        model.addAttribute("title", title);
        model.addAttribute("level", level);
        model.addAttribute("lastCatch", lastCatch);
        model.addAttribute("joinDate", user.getJoinDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        return "userProfile";
    }
}