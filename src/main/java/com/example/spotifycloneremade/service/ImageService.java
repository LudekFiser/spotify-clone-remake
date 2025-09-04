package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.CurrentUserDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    CurrentUserDto changeProfilePicture(MultipartFile image);
    void deleteProfilePicture();
}
