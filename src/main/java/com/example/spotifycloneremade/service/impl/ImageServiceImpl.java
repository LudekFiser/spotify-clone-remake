package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.CurrentUserDto;
import com.example.spotifycloneremade.dto.image.UploadedImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.mapper.UserMapper;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.service.ImageService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final CloudinaryService cloudinaryService;
    private final AvatarRepository avatarRepository;
    private final ProfileRepository profileRepository;

    @Override
    public CurrentUserDto changeProfilePicture(MultipartFile image) {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User is not authenticated");
        }
        var profile = profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();

        String uploadedPublicId = null;
        try {
            if (image == null || image.isEmpty()) {
                throw new RuntimeException("No image provided");
            }
            if(profile.getAvatar() != null){
                Avatar oldImage = profile.getAvatar();
                cloudinaryService.deleteImageByPublicId(oldImage.getPublicId());
                avatarRepository.delete(oldImage);
                profile.setAvatar(null);
            }

            // Uplaod new
            UploadedImageDto dto = cloudinaryService.uploadImage(image, "avatars");
            uploadedPublicId = dto.getPublicId();

            Avatar newImage = cloudinaryService.buildProfileImage(dto, profile);
            profile.setAvatar(newImage);
            avatarRepository.save(newImage);
            profileRepository.save(profile);

            currentUser.getProfile().setAvatar(newImage);
            userRepository.save(currentUser);

            return userMapper.toUserDto(currentUser);
        } catch (RuntimeException ex) {
            // DB fail -> zkus smazat hajzla v Cloudinary (best effort)
            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteImageByPublicId(uploadedPublicId);
                } catch (Exception cleanupEx) { log.warn("Cleanup of Cloudinary image failed: {}", cleanupEx.getMessage()); }
            }
            throw ex;
        }
    }

    @Override
    public void deleteProfilePicture() {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User is not authenticated");
        }

        var usersAvatar = currentUser.getProfile().getAvatar();
        if (usersAvatar == null) {
            throw new RuntimeException("You have no avatar");
        }


        var image = avatarRepository.findById(usersAvatar.getId()).orElseThrow();
        if (image.getId().equals(usersAvatar.getId())) {
            cloudinaryService.deleteImageByPublicId(image.getPublicId());
            avatarRepository.delete(image);
            currentUser.getProfile().setAvatar(null);
            userRepository.save(currentUser);
        }
    }
}
