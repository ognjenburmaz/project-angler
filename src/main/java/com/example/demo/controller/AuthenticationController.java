package com.example.demo.controller;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String verified, Model model) {
        model.addAttribute("loginUserDto", new LoginUserDto());
        if (verified != null) model.addAttribute("success", "Account verified! Please log in.");
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute LoginUserDto loginUserDto, HttpServletResponse response, Model model) {
        try {
            LoginResponse loginResponse = authenticationService.authenticate(loginUserDto);

            Cookie cookie = new Cookie("JWT", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/auth/login";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute RegisterUserDto registerUserDto, Model model) {
        try {
            authenticationService.signup(registerUserDto);

            return "redirect:/auth/verify?email=" + registerUserDto.getEmail();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("registerUserDto", new RegisterUserDto());
        return "register";
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam String email, Model model) {
        VerifyUserDto verifyUserDto = new VerifyUserDto();
        verifyUserDto.setEmail(email);
        model.addAttribute("verifyUserDto", verifyUserDto);
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@ModelAttribute VerifyUserDto verifyUserDto, Model model) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return "redirect:/auth/login?verified=true";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid or expired code");
            return "verify";
        }
    }

    @PostMapping("/resend")
    @ResponseBody
    public ResponseEntity<String> resendCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}