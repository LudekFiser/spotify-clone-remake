package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.enums.ROLE;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private ROLE role;
    private Boolean verified;
    private Boolean twoFactorEmail;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ImageDto avatar;          // tvůj existující DTO pro obrázky

    private ArtistResponse artist;
}
