package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.entity.LikedSong;
import com.example.spotifycloneremade.entity.Song;

import java.util.List;

public interface LikedSongService {

    boolean likeOrUnlikeSong(Long songId);
    List<SongSummaryDto> getCurrentUserLikedSongs();

}
