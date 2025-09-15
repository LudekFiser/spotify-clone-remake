package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
