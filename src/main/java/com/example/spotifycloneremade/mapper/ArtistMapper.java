package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.auth.ArtistResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArtistMapper {

    private final SongMapper songMapper;


    public ArtistResponse toArtistResponse(Artist artist) {
        // NIKDY nevrátí null → stream -> toList() dá [] když nic není
        var songSummaries = artist.getSongs()
                .stream()
                .map(songMapper::toSongResponse)
                .toList();

        return ArtistResponse.builder()
                .plays(artist.calculateTotalPlays())      // nebo artist.getPlays()
                .numOfSongs(artist.calculateNumberOfSongs()) // nebo artist.getNumOfSongs()
                .songs(songSummaries)
                .build();
    }

    public ArtistResponse toArtistPreviewResponse(Artist artist) {
        return ArtistResponse.builder()
                .plays(artist.calculateTotalPlays())
                .numOfSongs(artist.calculateNumberOfSongs())
                .songs(List.of())
                .build();
    }

}
