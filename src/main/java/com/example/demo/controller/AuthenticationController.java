package com.example.demo.controller;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.service.AuthenticationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;

@RequestMapping("/auth")
@Controller
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    Dotenv dotenv = Dotenv.load();
    String encryptionPassword = dotenv.get("ENCRYPTION_PASSWORD");
    String encryptionSalt = dotenv.get("ENCRYPTION_SALT");

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken))
            return "redirect:/";
        model.addAttribute("loginUserDto", new LoginUserDto());
        return "login";
    }

    @GetMapping(value = "/signup")
    public String register(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken))
            return "redirect:/";
        model.addAttribute("registerUserDto", new RegisterUserDto());
        return "register";
    }

    @GetMapping(value = "/verify")
    public String verifyEmail(
            @RequestParam String token,
            Model model
    ) {
        TextEncryptor encryptor = Encryptors.text(
                encryptionPassword,
                encryptionSalt
        );
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !(auth instanceof AnonymousAuthenticationToken))
                return "redirect:/";
            String decrypted = encryptor.decrypt(token);
            String[] parts = decrypted.split("\\|");
            String email = parts[0];
            long creationTime = Long.parseLong(parts[1]);

            boolean isExpired = Instant.now().toEpochMilli() - creationTime > 86_400_000;
            if (isExpired) {
                model.addAttribute("error", "This verification link has expired.");
                return "errorPage";
            }

            model.addAttribute("token", token);
            VerifyUserDto verifyUserDto = new VerifyUserDto();
            verifyUserDto.setEmail(email);
            verifyUserDto.setToken(token);
            model.addAttribute("verifyUserDto", verifyUserDto);
            return "verify";

        } catch (Exception e) {
            model.addAttribute("error", "Invalid verification link.");
            return "errorPage";
        }
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute RegisterUserDto registerUserDto, Model model) {
        if(authenticationService.isEmailAlreadyInUse(registerUserDto.getEmail())) {
            model.addAttribute("error", "This email is already in use.");
            return "register";
        }
        if(authenticationService.isUsernameAlreadyInUse(registerUserDto.getUsername())) {
            model.addAttribute("error", "This username is already in use.");
            return "register";
        }
        TextEncryptor encryptor = Encryptors.text(
                encryptionPassword,
                encryptionSalt
        );
        String token = encryptor.encrypt(registerUserDto.getEmail() + "|" + System.currentTimeMillis());
        authenticationService.signup(registerUserDto, token);

        return "redirect:/auth/verify?token=" + token;
    }

    @PostMapping("/verify")
    public String verifyUser(
            @ModelAttribute VerifyUserDto verifyUserDto,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            redirectAttributes.addFlashAttribute("success", "Account verified successfully!");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            VerifyUserDto verifyDto = new VerifyUserDto();
            verifyDto.setEmail(verifyUserDto.getEmail());
            verifyDto.setToken(verifyUserDto.getToken());

            model.addAttribute("email", verifyUserDto.getEmail());
            model.addAttribute("token", verifyUserDto.getToken());
            model.addAttribute("verifyUserDto", verifyDto);
            model.addAttribute("error", "Wrong code, please try again.");

            return "verify";
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email, @RequestParam String token) {
        try {
            authenticationService.resendVerificationCode(email, token);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}