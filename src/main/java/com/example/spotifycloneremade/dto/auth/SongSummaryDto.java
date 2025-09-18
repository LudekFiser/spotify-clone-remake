package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.dto.image.ImageDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SongSummaryDto {

    private Long id;
    private String title;
    private Integer duration;
    private LocalDate releaseDate;
    private Integer plays;
    private String url;
    private Long artistId;
    private String artistName;
    private ImageDto songImage;

    private boolean likedByCurrentUser;
}
