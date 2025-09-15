package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.auth.ArtistResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.entity.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongMapper {

    public SongSummaryDto toSongSummary(Song s) {
        return SongSummaryDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .duration(s.getDuration())
                .releaseDate(s.getReleaseDate())
                .plays(s.getPlays())
                .url(s.getUrl())
                .build();
    }

    public SongSummaryDto toSongResponse(Song s) {
        return SongSummaryDto.builder()
                .id(s.getId())
                .plays(s.getPlays())
                .title(s.getTitle())
                .duration(s.getDuration())
                .releaseDate(s.getReleaseDate())
                .build();
    }

    public Song toSongEntity(CreateSongRequest req) {
        return Song.builder()
                .title(req.getTitle())
                .genre(req.getGenre())
                .releaseDate(req.getReleaseDate())
                .build();
    }
}
