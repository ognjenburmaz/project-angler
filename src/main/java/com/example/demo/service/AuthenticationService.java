package com.example.demo.service;

import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationService(
            UserService userService,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input, String token) {
        User user = new User(input.getUsername(), input.getEmail(),
                    passwordEncoder.encode(input.getPassword()), Role.USER, 0, LocalDate.now());
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user, token);
        return userService.save(user);
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userService.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userService.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email, String token) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user, token);
            userService.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user, String token) {
        String returnUrl = "http://localhost:8080/auth/verify?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        String htmlMessage = """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <div style="background-color: #f5f5f5; padding: 20px;">
                <h2 style="color: #333;">Welcome to Fishing Buddy!</h2>
                <p style="font-size: 16px;">Please enter this verification code to activate your account:</p>
                <div style="background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <h3 style="color: #333;">Verification Code:</h3>
                    <p style="font-size: 24px; font-weight: bold; color: #007bff;">%s</p>
                </div>
                <p style="font-size: 14px; margin-top: 20px;">
                    <strong>Lost the code?</strong> 
                    <a href="%s" style="color: #007bff;">Click here to return to verification</a> 
                    (link expires in 15 minutes).
                </p>
            </div>
        </body>
        </html>
        """.formatted(user.getVerificationCode(), returnUrl);

        try {
            emailService.sendVerificationEmail(
                    user.getEmail(),
                    "Verify Your Fishing Buddy Account",
                    htmlMessage
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public boolean isEmailAlreadyInUse(String email) {
        Optional<User> optionalUser = userService.findByEmail(email);
        return optionalUser.isPresent();
    }

    public boolean isUsernameAlreadyInUse(String username) {
        Optional<User> optionalUser = userService.findByUsername(username);
        return optionalUser.isPresent();
    }
}