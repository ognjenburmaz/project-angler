package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class VerifyUserDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String verificationCode;
}