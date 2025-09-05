package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.Song;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

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
                .twoFactorEmail(profile.getTwoFactorEmail())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .avatar(toImageDto(profile.getAvatar()));

        if (profile.getArtist() != null) {
            b.artist(toArtistDetails(profile.getArtist()));
        }

        return b.build();
    }

    private ImageDto toImageDto(Avatar a) {
        return a == null ? null : new ImageDto(a.getImageUrl(), a.getPublicId());
    }

    private ArtistResponse toArtistDetails(Artist artist) {
        // getSongs() u tebe NIKDY nevrátí null → stream -> toList() dá [] když nic není
        var songSummaries = artist.getSongs()
                .stream()
                .map(this::toSongSummary)
                .toList();

        return ArtistResponse.builder()
                .plays(artist.calculateTotalPlays())      // nebo artist.getPlays()
                .numOfSongs(artist.calculateNumberOfSongs()) // nebo artist.getNumOfSongs()
                .songs(songSummaries)                     // vždy seznam (prázdný/naplněný)
                .build();
    }

    private SongSummaryDto toSongSummary(Song s) {
        return SongSummaryDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .duration(s.getDuration())
                .build();
    }
}

