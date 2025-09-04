package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.enums.ROLE;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
public class RegisterResponse {

    private Long id;
    private String email;
    private String name;
    private ROLE role;

    private String phoneNumber;
    private LocalDate dateOfBirth;

    private ProfileDto profile;
}
