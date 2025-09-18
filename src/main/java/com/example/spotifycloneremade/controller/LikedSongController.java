package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.entity.LikedSong;
import com.example.spotifycloneremade.service.LikedSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liked-songs")
@RequiredArgsConstructor
public class LikedSongController {

    private final LikedSongService likedSongService;

    @GetMapping
    public ResponseEntity<List<SongSummaryDto>> getLikedSongs() {
        List<SongSummaryDto> likedSongs = likedSongService.getCurrentUserLikedSongs();
        return ResponseEntity.ok(likedSongs);
    }

    @PostMapping("/like/{songId}")
    public ResponseEntity<Boolean> likeOrUnlikeSong(@PathVariable Long songId) {
        boolean isLiked = likedSongService.likeOrUnlikeSong(songId);
        return ResponseEntity.ok(isLiked);
    }
}
