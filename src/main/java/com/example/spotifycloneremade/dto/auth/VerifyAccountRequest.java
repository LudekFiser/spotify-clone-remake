package com.example.spotifycloneremade.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAccountRequest {
    @NotBlank(message = "Verification code is required")
    private String otp;
}
