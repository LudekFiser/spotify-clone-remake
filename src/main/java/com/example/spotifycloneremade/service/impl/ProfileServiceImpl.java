package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.enums.ROLE;
import com.example.spotifycloneremade.exception.NotOldEnoughException;
import com.example.spotifycloneremade.exception.PasswordsDoNotMatchException;
import com.example.spotifycloneremade.exception.PasswordsMatchingException;
import com.example.spotifycloneremade.mapper.ProfileMapper;
import com.example.spotifycloneremade.repository.ArtistRepository;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.ProfileService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final ArtistRepository artistRepo;
    private final AvatarRepository avatarRepo;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final CloudinaryService cloudinaryService;
    private final AuthService authService;

    /* ===== helpers ===== */

    private boolean emailAlreadyUsed(String email) {
        // email je teď v PROFILES
        return profileRepo.existsByEmail(email);
    }

    private boolean isAdult(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears() >= 18;
    }

    /* ===== register (pro USER i ARTIST) ===== */

    @Override
    public ProfileResponse register(RegisterRequest req) {
        if (emailAlreadyUsed(req.getEmail())) throw new IllegalArgumentException("Email is already used");
        if (!isAdult(req.getDateOfBirth()))  throw new NotOldEnoughException();

        // 1) Profile
        Profile profile = profileMapper.toEntity(req);
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile = profileRepo.save(profile);

        // 2) Detail podle role (shared PK = profile.id)
        if (req.getRole() == ROLE.ARTIST) {
            var artist = new Artist();
            artist.setProfile(profile);
            artist.setPlays(0);
            artist.setNumOfSongs(0);
            artistRepo.save(artist);
            profile.setArtist(artist); // aby mapper viděl navázání
        } else {
            var user = new User();
            user.setProfile(profile);
            userRepo.save(user);
            profile.setUser(user);
        }

        return profileMapper.toResponse(profile); // sjednocený response
    }

    /* ===== update profilu ===== */

    @Override
    public ProfileResponse updateUser(UpdateUserRequest req) {
        Profile me = authService.getCurrentProfile();

        if (req.getName() != null)        me.setName(req.getName());
        if (req.getEmail() != null) {
            if (!me.getEmail().equals(req.getEmail()) && emailAlreadyUsed(req.getEmail()))
                throw new IllegalArgumentException("Email is already used");
            me.setEmail(req.getEmail());
        }
        if (req.getTwoFactorEmail() != null) me.setTwoFactorEmail(req.getTwoFactorEmail());

        me.setUpdatedAt(LocalDateTime.now());
        profileRepo.save(me);

        return profileMapper.toResponse(me);
    }

    /* ===== smazání účtu ===== */

    @Override
    public void deleteUser(DeleteAccountDto otp) {
        Profile me = authService.getCurrentProfile();

        // OTP kontrola
        if (me.getVerificationCode() == null) throw new RuntimeException("Invalid OTP");
        if (!otpService.verifyOtp(otp.getOtp(), me.getVerificationCode())) throw new RuntimeException("Invalid OTP");
        if (me.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) throw new RuntimeException("OTP expired");

        // smazat avatar v cloudu (pokud je)
        if (me.getAvatar() != null) {
            var image = me.getAvatar();
            cloudinaryService.deleteImageByPublicId(image.getPublicId());
            me.setAvatar(null);
            profileRepo.save(me);
            avatarRepo.delete(image);
        }

        // smazání profilu → díky FK ON DELETE CASCADE spadnou User/Artist
        profileRepo.delete(me);
    }

    /* ===== změna hesla ===== */

    @Override
    public void changePassword(ChangePasswordRequest req) {
        Profile me = authService.getCurrentProfile();

        // heslo je v PROFILES
        if (!passwordEncoder.matches(req.getOldPassword(), me.getPassword()))
            throw new PasswordsDoNotMatchException();
        if (passwordEncoder.matches(req.getNewPassword(), me.getPassword()))
            throw new PasswordsMatchingException();

        // OTP kontrola
        if (me.getVerificationCode() == null) throw new RuntimeException("Invalid OTP");
        if (!otpService.verifyOtp(req.getOtp(), me.getVerificationCode())) throw new RuntimeException("Invalid OTP");
        if (me.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) throw new RuntimeException("OTP expired");

        me.setPassword(passwordEncoder.encode(req.getNewPassword()));
        me.setVerificationCode(null);
        me.setVerificationCodeExpiration(null);
        profileRepo.save(me);
    }

    /* ===== verifikace účtu ===== */

    @Override
    public void verifyAccount(VerifyAccountRequest req) {
        Profile me = authService.getCurrentProfile();

        if (me.isVerified()) throw new RuntimeException("Account is already verified");
        if (me.getVerificationCode() == null) throw new RuntimeException("Invalid OTP");
        if (!otpService.verifyOtp(req.getOtp(), me.getVerificationCode())) throw new RuntimeException("Invalid OTP");
        if (me.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) throw new RuntimeException("OTP expired");

        me.setVerificationCode(null);
        me.setVerificationCodeExpiration(null);
        me.setVerified(true);
        profileRepo.save(me);
    }
}

