package com.example.spotifycloneremade.dto.auth;

import com.example.spotifycloneremade.entity.Song;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArtistResponse {

    private Integer plays;
    private Integer numOfSongs;
    // volitelné: rychlé info o písničkách (ne full entity!)
    private List<SongSummaryDto> songs;
}
