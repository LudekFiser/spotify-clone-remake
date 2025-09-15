package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.dto.song.CreateSongResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {

    SongSummaryDto uploadSong(CreateSongRequest req, MultipartFile file, MultipartFile image);


    SongSummaryDto findBySongId(Long id);
}
