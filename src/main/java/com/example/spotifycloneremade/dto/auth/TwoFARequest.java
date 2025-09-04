package com.example.spotifycloneremade.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFARequest {

    @NotNull(message = "User is required")
    private Long userId;

    @NotBlank(message = "OTP is required")
    private String otp;
}
