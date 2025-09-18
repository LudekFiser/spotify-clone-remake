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

    private final ImageMapper imageMapper;

    public SongSummaryDto toSongSummary(Song s, boolean likedByCurrentUser) {
        return SongSummaryDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .duration(s.getDuration())
                .releaseDate(s.getReleaseDate())
                .plays(s.getPlays())
                .url(s.getUrl())
                .artistId(s.getArtist().getProfile().getId())
                .artistName(s.getArtist().getProfile().getName())
                .songImage(imageMapper.toSongImageDto(s.getSongImage()))
                .likedByCurrentUser(likedByCurrentUser)
                .build();
    }

    public SongSummaryDto toSongResponse(Song s) {
        return SongSummaryDto.builder()
                .id(s.getId())
                .plays(s.getPlays())
                .title(s.getTitle())
                .duration(s.getDuration())
                .releaseDate(s.getReleaseDate())
                .artistId(s.getArtist().getProfile().getId())
                .artistName(s.getArtist().getProfile().getName())
                .url(s.getUrl())
                .songImage(imageMapper.toSongImageDto(s.getSongImage()))
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
