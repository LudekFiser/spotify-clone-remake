package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.enums.ROLE;
import com.example.spotifycloneremade.validation.Lowercase;
import com.example.spotifycloneremade.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required!")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Email is required!")
    @Email(message = "Enter a valid email...")
    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long!")
    @ValidPassword(message = "Password must contain at least one uppercase letter, one digit and one special character")
    private String password;

    @NotBlank(message = "Phone number is required!")
    private String phoneNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Date of birth is required!")
    private LocalDate dateOfBirth;

    @NotNull(message = "ROLE is required!")
    private ROLE role;

}
