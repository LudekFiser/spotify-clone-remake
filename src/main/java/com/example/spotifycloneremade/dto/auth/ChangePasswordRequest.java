package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long!")
    @ValidPassword(message = "Password must contain at least one uppercase letter, one digit and one special character")
    private String newPassword;
}
