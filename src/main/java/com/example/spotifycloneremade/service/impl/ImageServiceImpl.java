package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.ProfileResponse;
import com.example.spotifycloneremade.dto.image.UploadedImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.mapper.ProfileMapper;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
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
        Profile currentUser = authService.getCurrentProfile();

        String uploadedPublicId = null;

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("No image provided");
        }

        try {
            // 1) pokud má profil avatar, smaž ho (cloud + DB) a odpoj
            if (currentUser.getAvatar() != null) {
                Avatar old = currentUser.getAvatar();
                try {
                    cloudinaryService.deleteImageByPublicId(old.getPublicId());
                } catch (Exception ignore) { /* best effort */ }
                currentUser.setAvatar(null);
                profileRepository.save(currentUser);
                avatarRepository.delete(old);
            }

            // 2) upload nového
            UploadedImageDto uploaded = cloudinaryService.uploadImage(image, "avatars");
            uploadedPublicId = uploaded.getPublicId();

            Avatar newAvatar = cloudinaryService.buildProfileImage(uploaded, currentUser);
            avatarRepository.save(newAvatar);    // persistneme obrázek
            currentUser.setAvatar(newAvatar);             // nastavíme aktuální avatar na profilu
            profileRepository.save(currentUser);          // uložíme profil

            // 3) response přes sjednocený mapper
            return profileMapper.toResponse(currentUser);

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
    public ProfileResponse deleteProfilePicture() {
        Profile currentUser = authService.getCurrentProfile();

        Avatar userAvatar = currentUser.getAvatar();
        if (userAvatar == null) {
            // ať je endpoint idempotentní – vrať aktuální profil
            return profileMapper.toResponse(currentUser);
        }

        // 1) cloudinary – best effort
        try {
            cloudinaryService.deleteImageByPublicId(userAvatar.getPublicId());
        } catch (Exception ignore) { }

        // 2) odpoj z profilu (důležité kvůli FK avatar_id v profiles)
        currentUser.setAvatar(null);
        profileRepository.save(currentUser);

        // 3) smaž řádek z avatars (můžeš spoléhat na orphanRemoval, ale explicitně je to nejčistší)
        avatarRepository.delete(userAvatar);

        // 4) vrať aktualizovaný stav
        return profileMapper.toResponse(currentUser);
    }

}