package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.dto.auth.resetPassword.ResetPasswordRequest;
import com.example.spotifycloneremade.entity.*;
import com.example.spotifycloneremade.enums.ROLE;
import com.example.spotifycloneremade.exception.*;
import com.example.spotifycloneremade.mapper.ProfileMapper;
import com.example.spotifycloneremade.mapper.SongMapper;
import com.example.spotifycloneremade.repository.*;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.ProfileService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final AvatarRepository avatarRepository;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final CloudinaryService cloudinaryService;
    private final AuthService authService;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final LikedSongRepository likedSongRepository;


    private boolean emailAlreadyUsed(String email) {
        return profileRepository.existsByEmail(email);
    }


    @Override
    public ProfileResponse register(RegisterRequest req) {
        if (emailAlreadyUsed(req.getEmail())) {
            throw new IllegalArgumentException("Email is already used");
        }
        if (!Profile.isAdult(req.getDateOfBirth()))  {
            throw new NotOldEnoughException();
        }

        Profile profile = profileMapper.toEntity(req);
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile = profileRepository.save(profile);

        if (req.getRole() == ROLE.ARTIST) {
            var artist = new Artist();
            artist.setProfile(profile);
            artist.setPlays(0);
            artist.setNumOfSongs(0);
            artistRepository.save(artist);
            profile.setArtist(artist);
        } else {
            var user = new User();
            user.setProfile(profile);
            user.setLikedSongsCount(0);
            userRepository.save(user);
            profile.setUser(user);
        }

        return profileMapper.toResponse(profile);
    }


    @Override
    public ProfileResponse updateUser(UpdateUserRequest req) {
        Profile currentProfile = authService.getCurrentProfile();

        if (req.getName() != null) {
            currentProfile.setName(req.getName());
        }
        if (req.getEmail() != null) {
            if (!currentProfile.getEmail().equals(req.getEmail()) && emailAlreadyUsed(req.getEmail()))
                throw new IllegalArgumentException("Email is already used");
            currentProfile.setEmail(req.getEmail());
        }
        if (req.getTwoFactorEmail() != null) currentProfile.setTwoFactorEmail(req.getTwoFactorEmail());

        currentProfile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(currentProfile);

        return profileMapper.toResponse(currentProfile);
    }


    @Override
    public void deleteUser(DeleteAccountDto otp) {
        Profile currentProfile = authService.getCurrentProfile();

        // OTP kontrola
        if (currentProfile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(otp.getOtp(), currentProfile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (currentProfile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (currentProfile.getAvatar() != null) {
            var image = currentProfile.getAvatar();
            cloudinaryService.deleteImageByPublicId(image.getPublicId());
            currentProfile.setAvatar(null);
            profileRepository.save(currentProfile);
            avatarRepository.delete(image);
        }

        profileRepository.delete(currentProfile);
    }

    @Override
    public void forgotPassword(ResetPasswordRequest req) {

        if(!req.getNewPassword().equals(req.getRepeatNewPassword())) {
            throw new PasswordResetReqNotMatching();
        }

        Profile profile = profileRepository.findByEmail(req.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (profile.getVerificationCode() == null ||
                profile.getVerificationCodeExpiration() == null ||
                profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now()) ||
                !otpService.verifyOtp(req.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid or expired OTP");
        }


        profile.setPassword(passwordEncoder.encode(req.getNewPassword()));
        profile.setVerificationCode(null);
        profile.setVerificationCodeExpiration(null);
        profileRepository.save(profile);
    }




    /*@Override
    public SearchResultResponse searchProfiles(ROLE role, String artistName, String songName) {
        List<Profile> profiles = new ArrayList<>();
        List<Song> songs = new ArrayList<>();

        boolean isArtistNameEmpty = (artistName == null || artistName.isBlank());
        boolean isSongNameEmpty = (songName == null || songName.isBlank());

        // Pokud je zadán artist name
        if (!isArtistNameEmpty) {
            profiles = profileRepository.findByRoleAndName(role, artistName);

            // pokud není hledání podle songName, přidej jejich songy
            if (isSongNameEmpty) {
                songs = profiles.stream()
                        .filter(p -> p.getArtist() != null)
                        .flatMap(p -> p.getArtist().getSongs().stream())
                        .distinct()
                        .toList();
            }
        }

        // Pokud je zadán song name
        if (!isSongNameEmpty) {
            //songs = songRepository.findByTitle(songName);
            songs = songRepository.findByTitleWithArtistAndImage(songName);


            // Najdi i profily vlastnící nalezené songy
            List<Profile> songOwners = songs.stream()
                    .map(Song::getArtist)
                    .filter(Objects::nonNull)
                    .map(Artist::getProfile)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            for (Profile p : songOwners) {
                if (!profiles.contains(p)) {
                    profiles.add(p);
                }
            }
        }

        // Když není zadáno nic
        if (isArtistNameEmpty && isSongNameEmpty) {
            profiles = profileRepository.findByRoleOnly(role);

            songs = profiles.stream()
                    .filter(p -> p.getArtist() != null)
                    .flatMap(p -> p.getArtist().getSongs().stream())
                    .distinct()
                    .toList();
        }

        return SearchResultResponse.builder()
                .profiles(profiles.stream().map(profileMapper::toSearchResponse).toList())
                .songs(songs.stream().map(songMapper::toSongSummary).toList())
                .build();
    }*/

    @Override
    public SearchResultResponse searchProfiles(ROLE role, String artistName, String songName) {
        List<Profile> profiles = new ArrayList<>();
        List<Song> songs = new ArrayList<>();

        boolean isArtistNameEmpty = (artistName == null || artistName.isBlank());
        boolean isSongNameEmpty = (songName == null || songName.isBlank());

        if (!isArtistNameEmpty) {
            profiles = profileRepository.findByRoleAndName(role, artistName);

            if (isSongNameEmpty) {
                songs = profiles.stream()
                        .filter(p -> p.getArtist() != null)
                        .flatMap(p -> p.getArtist().getSongs().stream())
                        .distinct()
                        .toList();
            }
        }

        if (!isSongNameEmpty) {
            songs = songRepository.findByTitleWithArtistAndImage(songName);

            List<Profile> songOwners = songs.stream()
                    .map(Song::getArtist)
                    .filter(Objects::nonNull)
                    .map(Artist::getProfile)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            for (Profile p : songOwners) {
                if (!profiles.contains(p)) {
                    profiles.add(p);
                }
            }
        }

        if (isArtistNameEmpty && isSongNameEmpty) {
            profiles = profileRepository.findByRoleOnly(role);

            songs = profiles.stream()
                    .filter(p -> p.getArtist() != null)
                    .flatMap(p -> p.getArtist().getSongs().stream())
                    .distinct()
                    .toList();
        }

        // Získání přihlášeného uživatele (pokud je)
        Profile currentProfile = null;
        try {
            currentProfile = authService.getCurrentProfile();
        } catch (Exception ignored) {
        }

        Profile finalCurrentProfile = currentProfile;
        return SearchResultResponse.builder()
                .profiles(profiles.stream()
                        .map(profileMapper::toSearchResponse)
                        .toList())
                .songs(songs.stream()
                        .map(song -> {
                            boolean isLiked = false;
                            if (finalCurrentProfile != null) {
                                isLiked = likedSongRepository.existsById(
                                        new LikedSongId(finalCurrentProfile.getId(), song.getId()));
                            }
                            return songMapper.toSongSummary(song, isLiked);
                        })
                        .toList())
                .build();
    }










    @Override
    public SearchProfileResponse findByProfileId(Long id) {
        var profile = profileRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return profileMapper.toSearchResponse(profile);
    }


    @Override
    public void changePassword(ChangePasswordRequest req) {
        Profile currentProfile = authService.getCurrentProfile();

        if (!passwordEncoder.matches(req.getOldPassword(), currentProfile.getPassword()))
            throw new PasswordsDoNotMatchException();
        if (passwordEncoder.matches(req.getNewPassword(), currentProfile.getPassword()))
            throw new PasswordsMatchingException();

        // OTP validation
        if (currentProfile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(req.getOtp(), currentProfile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (currentProfile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        currentProfile.setPassword(passwordEncoder.encode(req.getNewPassword()));
        currentProfile.setVerificationCode(null);
        currentProfile.setVerificationCodeExpiration(null);
        profileRepository.save(currentProfile);
    }


    @Override
    public void verifyAccount(VerifyAccountRequest req) {
        Profile currentProfile = authService.getCurrentProfile();

        if (currentProfile.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }
        if (currentProfile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(req.getOtp(), currentProfile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (currentProfile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        currentProfile.setVerificationCode(null);
        currentProfile.setVerificationCodeExpiration(null);
        currentProfile.setVerified(true);
        profileRepository.save(currentProfile);
    }
}

