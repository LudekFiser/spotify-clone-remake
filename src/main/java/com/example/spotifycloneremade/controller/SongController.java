package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.PlaySongDto;
import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Tag(name = "songs")
@CrossOrigin(origins = "http://localhost:5173")
public class SongController {

    private final SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<SongSummaryDto> uploadSong(@RequestPart("req") CreateSongRequest req,
                                                     @RequestPart("file") MultipartFile file,
                                                     @RequestPart("image") MultipartFile image) {
        var uploadSong = songService.uploadSong(req, file, image);
        return ResponseEntity.ok(uploadSong);
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongSummaryDto> getSong(@PathVariable Long songId) {
        //return profileService.findByProfileId(userId);
        var song = songService.findBySongId(songId);
        //return ResponseEntity.ok(profileService.findByProfileId(userId));
        return ResponseEntity.ok(song);
    }

    @GetMapping("/artists/{artistId}/songs")
    public ResponseEntity<List<SongSummaryDto>> getSongsByArtist(@PathVariable Long artistId) {
        List<SongSummaryDto> songs = songService.getSongsByArtist(artistId);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/play/{songId}")
    public ResponseEntity<PlaySongDto> playSong(@PathVariable Long songId) {
        var song = songService.playSong(songId);
        return ResponseEntity.ok(song);
    }

}
