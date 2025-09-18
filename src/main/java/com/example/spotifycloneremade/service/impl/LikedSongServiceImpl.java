package com.example.spotifycloneremade.service.impl;


import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.entity.LikedSong;
import com.example.spotifycloneremade.entity.LikedSongId;
import com.example.spotifycloneremade.entity.Song;
import com.example.spotifycloneremade.mapper.ImageMapper;
import com.example.spotifycloneremade.mapper.SongMapper;
import com.example.spotifycloneremade.repository.LikedSongRepository;
import com.example.spotifycloneremade.repository.SongRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.LikedSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikedSongServiceImpl implements LikedSongService {

    private final AuthService authService;
    private final SongRepository songRepository;
    private final LikedSongRepository likedSongRepository;
    private final UserRepository userRepository;
    private final SongMapper songMapper;

    @Override
    @Transactional
    public boolean likeOrUnlikeSong(Long songId) {
        var currentProfile = authService.getCurrentProfile();
        if (currentProfile == null) {
            throw new IllegalStateException("Current profile is null");
        }
        var song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        var user = currentProfile.getUser();
        if (user == null) {
            throw new IllegalStateException("Only USER profiles can like songs.");
        }

        LikedSongId id = new LikedSongId(currentProfile.getId(), songId);

        boolean isLiked;

        if (likedSongRepository.existsById(id)) {
            // Un-like
            likedSongRepository.deleteById(id);
            song.setLikes(song.getLikes() - 1);
            user.setLikedSongsCount(user.getLikedSongsCount() - 1);
            isLiked = false;
        } else {
            // Like
            LikedSong liked = new LikedSong();
            liked.setUser(user);
            liked.setSong(song);
            liked.setId(new LikedSongId(currentProfile.getId(), song.getId()));
            liked.setLikedAt(LocalDateTime.now());

            likedSongRepository.save(liked);
            song.setLikes(song.getLikes() + 1);
            user.setLikedSongsCount(user.getLikedSongsCount() + 1);
            isLiked = true;
        }

        songRepository.save(song);
        userRepository.save(user);

        return isLiked;
    }


    @Override
    public List<SongSummaryDto> getCurrentUserLikedSongs() {
        var currentProfile = authService.getCurrentProfile();
        var likedSongs = likedSongRepository.findByUserProfileId(currentProfile.getId());

        return likedSongs.stream()
                .map(liked -> songMapper.toSongSummary(liked.getSong(), true))
                .toList();
    }



}
