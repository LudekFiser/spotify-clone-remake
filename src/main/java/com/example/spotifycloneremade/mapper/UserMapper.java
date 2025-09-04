package com.example.spotifycloneremade.mapper;


import com.example.spotifycloneremade.dto.auth.CurrentUserDto;
import com.example.spotifycloneremade.dto.auth.ProfileDto;
import com.example.spotifycloneremade.dto.auth.RegisterRequest;
import com.example.spotifycloneremade.dto.auth.RegisterResponse;
import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    public RegisterResponse toResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .profile(toProfileDto(user.getProfile()))
                .build();
    }
    public ProfileDto toProfileDto(Profile profile) {
        if (profile == null) return null;

        ImageDto avatarDto = null;
        if (profile.getAvatar() != null) {
            avatarDto = new ImageDto(
                    profile.getAvatar().getImageUrl(),
                    profile.getAvatar().getPublicId()
            );
        }

        return ProfileDto.builder()
                .isVerified(profile.isVerified())
                .twoFactorEmail(profile.getTwoFactorEmail())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .avatar(avatarDto)
                .build();
    }



    public CurrentUserDto toUserDto(User user) {
        return CurrentUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                /*.avatarId(new ImageDto(
                        user.getProfile().getAvatar().getImageUrl(),
                        user.getProfile().getAvatar().getPublicId(),
                        user.getProfile().getAvatar().getOrd()))*/
                .profile(toProfileDto(user.getProfile()))
                .build();
    }
}
