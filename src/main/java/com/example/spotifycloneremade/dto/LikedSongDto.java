package com.example.spotifycloneremade.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedSongDto {

    private Long songId;
    private boolean liked;
}

