package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ImageMapper imageMapper;
    private final ArtistMapper artistMapper;

    public Profile toEntity(RegisterRequest req) {
        Profile p = new Profile();
        p.setName(req.getName());
        p.setEmail(req.getEmail());
        p.setPassword(req.getPassword());
        p.setDateOfBirth(req.getDateOfBirth());
        p.setRole(req.getRole());
        p.setVerified(false);
        p.setTwoFactorEmail(false);
        return p;
    }

    public ProfileResponse toResponse(Profile profile) {
        var b = ProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .role(profile.getRole())
                .verified(profile.isVerified())
                .twoFactorEmail(profile.isTwoFactorEmail())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .avatar(imageMapper.toImageDto(profile.getAvatar()));

        if (profile.getArtist() != null) {
            b.artist(artistMapper.toArtistResponse(profile.getArtist()));
        }

        return b.build();
    }

    /*public SearchProfileResponse toSearchResponse(Profile profile) {
        return SearchProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .role(profile.getRole())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .avatar(imageMapper.toImageDto(profile.getAvatar()))
                .artist(profile.getArtist() != null ? artistMapper.toArtistResponse(profile.getArtist()) : null)
                .build();
    }*/
    public SearchProfileResponse toSearchResponse(Profile profile) {
        return SearchProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .role(profile.getRole())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .avatar(imageMapper.toImageDto(profile.getAvatar()))
                .artist(profile.getArtist() != null ? artistMapper.toArtistPreviewResponse(profile.getArtist()) : null)
                .build();
    }


}

