package com.example.demo.service;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.enums.Role;
import com.example.demo.jwt.JwtService;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }

        User user = User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .role(Role.USER)
                .totalCatches(0)
                .joinDate(LocalDate.now())
                .enabled(false)
                .verificationCode(generateVerificationCode())
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        User savedUser = userRepository.save(user);

        sendVerificationEmail(savedUser);

        return savedUser;
    }

    public LoginResponse authenticate(LoginUserDto input) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(user);
        return new LoginResponse(jwtToken, 86400000);
    }

    public void verifyUser(VerifyUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired");
        }
        if (!user.getVerificationCode().equals(input.getVerificationCode())) {
            throw new RuntimeException("Invalid code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.isEnabled()) {
            throw new RuntimeException("This account is already verified.");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);
        sendVerificationEmail(user);
    }

    private void sendVerificationEmail(User user) {
        String subject = "Verify your Fishing Buddy Account";
        String htmlMessage = String.format("""
        <html>
        <body style="font-family: 'Poppins', sans-serif; background-color: #f9fafb; padding: 40px; text-align: center;">
            <div style="max-width: 400px; margin: 0 auto; background: white; padding: 30px; border-radius: 24px; shadow: 0 4px 6px rgba(0,0,0,0.05);">
                <h2 style="color: #1e40af; margin-bottom: 8px;">Welcome, %s!</h2>
                <p style="color: #6b7280; font-size: 14px;">Use the code below to verify your account.</p>
                <div style="background: #eff6ff; padding: 20px; border-radius: 16px; margin: 24px 0;">
                    <span style="font-size: 32px; font-weight: 900; letter-spacing: 8px; color: #2563eb;">%s</span>
                </div>
                <p style="color: #9ca3af; font-size: 12px;">This code expires in 15 minutes.</p>
            </div>
        </body>
        </html>
        """, user.getUsername(), user.getVerificationCode());

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            System.err.println("Email Error: " + e.getMessage());
            throw new RuntimeException("We couldn't send the verification email. Please try again.");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public boolean isEmailAlreadyInUse(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    public boolean isUsernameAlreadyInUse(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.isPresent();
    }
}