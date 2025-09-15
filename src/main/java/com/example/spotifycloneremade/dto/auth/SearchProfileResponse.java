package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.enums.ROLE;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SearchProfileResponse {

    private Long id;
    private String name;
    private String email;
    private ROLE role;

    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;

    private ImageDto avatar;
    private ArtistResponse artist;
}
