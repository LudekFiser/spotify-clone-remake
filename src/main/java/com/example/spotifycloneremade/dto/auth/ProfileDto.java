package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.dto.image.ImageDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProfileDto {
    private boolean isVerified;
    private Boolean twoFactorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ImageDto avatar;
}

