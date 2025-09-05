package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.ProfileResponse;
import com.example.spotifycloneremade.dto.image.UploadedImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.mapper.ProfileMapper;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.ImageService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AuthService authService;
    private final CloudinaryService cloudinaryService;
    private final AvatarRepository avatarRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public ProfileResponse changeProfilePicture(MultipartFile image) {
        Profile me = authService.getCurrentProfile();

        String uploadedPublicId = null;

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("No image provided");
        }

        try {
            // 1) pokud má profil avatar, smaž ho (cloud + DB) a odpoj
            if (me.getAvatar() != null) {
                Avatar old = me.getAvatar();
                try {
                    cloudinaryService.deleteImageByPublicId(old.getPublicId());
                } catch (Exception ignore) { /* best effort */ }
                me.setAvatar(null);
                profileRepository.save(me);
                avatarRepository.delete(old);
            }

            // 2) upload nového
            UploadedImageDto uploaded = cloudinaryService.uploadImage(image, "avatars");
            uploadedPublicId = uploaded.getPublicId();

            Avatar newAvatar = cloudinaryService.buildProfileImage(uploaded, me);
            avatarRepository.save(newAvatar);    // persistneme obrázek
            me.setAvatar(newAvatar);             // nastavíme aktuální avatar na profilu
            profileRepository.save(me);          // uložíme profil

            // 3) response přes sjednocený mapper
            return profileMapper.toResponse(me);

        } catch (RuntimeException ex) {
            // DB fail -> zkus smazat v Cloudinary (best effort)
            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteImageByPublicId(uploadedPublicId);
                } catch (Exception cleanupEx) {
                    log.warn("Cloudinary cleanup failed: {}", cleanupEx.getMessage());
                }
            }
            throw ex;
        }
    }

    @Override
    @Transactional
    public void deleteProfilePicture() {
        Profile me = authService.getCurrentProfile();

        Avatar usersAvatar = me.getAvatar();
        if (usersAvatar == null) {
            throw new RuntimeException("You have no avatar");
        }

        // pro jistotu načti z DB (když chceš validovat existenci)
        Avatar image = avatarRepository.findById(usersAvatar.getId())
                .orElseThrow(() -> new RuntimeException("Avatar not found"));

        try {
            cloudinaryService.deleteImageByPublicId(image.getPublicId());
        } catch (Exception ignore) { /* best effort */ }

    }
}