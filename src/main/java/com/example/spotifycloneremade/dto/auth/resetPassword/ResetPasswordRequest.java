package com.example.spotifycloneremade.dto.auth.resetPassword;

import com.example.spotifycloneremade.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "Email is required!")
    @Email(message = "Enter a valid email...")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long!")
    @ValidPassword(message = "Password must contain at least one uppercase letter, one digit and one special character")
    private String newPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long!")
    @ValidPassword(message = "Password must contain at least one uppercase letter, one digit and one special character")
    private String repeatNewPassword;
}

