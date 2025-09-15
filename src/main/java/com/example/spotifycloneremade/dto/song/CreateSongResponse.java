package com.example.spotifycloneremade.dto.song;

import com.example.spotifycloneremade.dto.auth.ArtistResponse;
import com.example.spotifycloneremade.dto.auth.ProfileDto;
import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.enums.ROLE;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSongResponse {

    private Long id;
    private String title;
    private String genre;

    private ImageDto songImage;
    private ArtistResponse artist;
}
