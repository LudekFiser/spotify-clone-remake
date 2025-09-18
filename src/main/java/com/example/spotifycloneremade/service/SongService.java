package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.PlaySongDto;
import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.dto.song.CreateSongResponse;
import com.example.spotifycloneremade.entity.LikedSong;
import com.example.spotifycloneremade.entity.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {

    SongSummaryDto uploadSong(CreateSongRequest req, MultipartFile file, MultipartFile image);

    SongSummaryDto findBySongId(Long id);

    List<SongSummaryDto> getSongsByArtist(Long artistId);

    //List<LikedSong> likeSong(Long songId);


    PlaySongDto playSong(Long id);
}
