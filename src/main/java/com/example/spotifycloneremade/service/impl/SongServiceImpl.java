package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.dto.auth.SongSummaryDto;
import com.example.spotifycloneremade.dto.image.UploadedImageDto;
import com.example.spotifycloneremade.dto.song.CreateSongRequest;
import com.example.spotifycloneremade.dto.song.CreateSongResponse;
import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Song;
import com.example.spotifycloneremade.entity.SongImage;
import com.example.spotifycloneremade.enums.ROLE;
import com.example.spotifycloneremade.enums.SongType;
import com.example.spotifycloneremade.exception.UserNotFoundException;
import com.example.spotifycloneremade.mapper.ArtistMapper;
import com.example.spotifycloneremade.mapper.SongMapper;
import com.example.spotifycloneremade.repository.ArtistRepository;
import com.example.spotifycloneremade.repository.SongRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.SongService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    private final AuthService authService;
    private final SongMapper songMapper;
    private final CloudinaryService cloudinaryService;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    /*@Override
    public SongSummaryDto uploadSong(CreateSongRequest req, MultipartFile file, MultipartFile image) {
        var currentUser = authService.getCurrentProfile();

        if (currentUser.getRole().equals(ROLE.USER)) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        var song = songMapper.toSongEntity(req);
        song.setPlays(0);

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("No image provided");
        }

        // Upload image
        UploadedImageDto uploaded = cloudinaryService.uploadImage(image, "song_images");
        SongImage songImage = cloudinaryService.buildSongImage(uploaded, song);
        song.setSongImage(songImage);

        int duration = Song.getDurationFromMultipartFile(file);
        song.setDuration(duration);

        // Save song first
        songRepository.save(song);

        // Add song to artist via helper method
        Artist artist = currentUser.getArtist();
        artist.addSong(song);
        artistRepository.save(artist);

        return songMapper.toSongResponse(song);
    }*/

    @Override
    public SongSummaryDto uploadSong(CreateSongRequest req, MultipartFile file, MultipartFile image) {
        var currentUser = authService.getCurrentProfile();

        if (currentUser.getRole().equals(ROLE.USER)) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        var song = songMapper.toSongEntity(req);
        song.setPlays(0);

        // Add artist to a song
        Artist artist = currentUser.getArtist();
        song.setArtist(artist);

        // Upload song file (audio)
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No audio file provided");
        }
        UploadedImageDto uploadedAudio = cloudinaryService.uploadSong(file, "songs");

        song.setUrl(uploadedAudio.getUrl());
        song.setType(SongType.SINGLE);
        song.setPublicId(uploadedAudio.getPublicId());
        //song.setDuration(Song.getDurationFromMultipartFile(file));
        int duration = Song.getDurationFromMultipartFile(file);
        System.out.println("⏱️ Duration in seconds: " + duration);
        song.setDuration(duration);

        // Uložení songu bez obrázku – kvůli ID
        songRepository.save(song);

        // Upload song image (cover)
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("No image provided");
        }
        UploadedImageDto uploadedImage = cloudinaryService.uploadImage(image, "song_images");

        SongImage songImage = cloudinaryService.buildSongImage(uploadedImage, song);
        song.setSongImage(songImage); // nastavíme obrázek v songu

        // Uložení songu znovu – tentokrát i s image
        songRepository.save(song);

        // Přidání songu do Artist (obousměrná vazba)
        artist.addSong(song);
        artistRepository.save(artist);

        return songMapper.toSongResponse(song);
    }

    @Override
    public SongSummaryDto findBySongId(Long id) {
        var song = songRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return songMapper.toSongResponse(song);
    }


}
