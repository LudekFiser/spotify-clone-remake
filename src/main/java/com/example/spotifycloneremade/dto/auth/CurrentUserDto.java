package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CurrentUserDto {
    private Long id;
    private String name;
    private String email;
    private ROLE role;

    private String phoneNumber;
    private LocalDate dateOfBirth;

    private ProfileDto profile;
}
