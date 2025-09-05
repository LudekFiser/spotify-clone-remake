package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.CurrentUserDto;
import com.example.spotifycloneremade.dto.auth.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ProfileResponse changeProfilePicture(MultipartFile image);
    void deleteProfilePicture();
}
