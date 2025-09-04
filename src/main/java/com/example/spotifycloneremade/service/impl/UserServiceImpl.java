package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.dto.image.UploadedImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.enums.ROLE;
import com.example.spotifycloneremade.exception.NotOldEnoughException;
import com.example.spotifycloneremade.exception.PasswordsDoNotMatchException;
import com.example.spotifycloneremade.exception.PasswordsMatchingException;
import com.example.spotifycloneremade.exception.UserNotFoundException;
import com.example.spotifycloneremade.mapper.UserMapper;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.service.UserService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthService authService;
    private final OtpService otpService;
    private final CloudinaryService cloudinaryService;
    private final AvatarRepository avatarRepository;
    private final ProfileRepository profileRepository;

    // DONE
    @Override
    public RegisterResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException();
        }

        if(!User.isAdult(req.getDateOfBirth())) {
            throw new NotOldEnoughException();
        }

        Profile profile = new Profile();
        profile.setVerificationCode(null);
        profile.setVerified(false);
        profile.setTwoFactorEmail(false);
        profile.setAvatar(null);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        var savedProfile = profileRepository.save(profile);

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (req.getRole().equals(ROLE.ARTIST)) {
            user.setRole(ROLE.ARTIST);
        } else {
            user.setRole(ROLE.USER);
        }
        user.setProfile(savedProfile);

        userRepository.save(user);

        /*try {
            emailService.sendPostRegisterEmail(savedUser.getEmail(), savedUser.getName());
        } catch (MessagingException e) {
            log.warn("Failed to send registration email to {}: {}", savedUser.getEmail(), e.getMessage());
        }*/
        return userMapper.toResponse(user);
    }

    // DONE
    @Override
    public RegisterResponse updateUser(UpdateUserRequest req) {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User is not authenticated");
        }
        var profile = profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();
        if (req.getName() != null) {
            currentUser.setName(req.getName());
        }
        if (req.getEmail() != null) {
            currentUser.setEmail(req.getEmail());
        }
        if (req.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(req.getPhoneNumber());
        }
        if (req.getTwoFactorEmail() != null) {
            //currentUser.getProfile().setTwoFactorEmail(req.getTwoFactorEmail());
            profile.setTwoFactorEmail(req.getTwoFactorEmail());
        }
        profile.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        profileRepository.save(profile);
        return userMapper.toResponse(currentUser);
    }

    // DONE
    @Override
    public void deleteUser(DeleteAccountDto otp) {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User is not authenticated");
        }
        var profile =  profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();
        if (profile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(otp.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid gaga OTP");
        }
        if (profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (profile.getAvatar() != null) {
            Avatar image = profile.getAvatar();

            cloudinaryService.deleteImageByPublicId(image.getPublicId());

            profile.setAvatar(null);
            profileRepository.save(profile);
            avatarRepository.delete(image);
        }

        userRepository.delete(currentUser);
    }

    // TODO
    @Override
    public void changePassword(ChangePasswordRequest req) {
        //var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User is not authenticated");
        }
        var profile = profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();

        // Password validations
        if(!passwordEncoder.matches(req.getOldPassword(), currentUser.getPassword())) {
            throw new PasswordsDoNotMatchException();
        }
        if(passwordEncoder.matches(req.getNewPassword(), currentUser.getPassword())) {
            throw new PasswordsMatchingException();
        }

        // validating user otp
        if (profile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(req.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        currentUser.setPassword(passwordEncoder.encode(req.getNewPassword()));
        profile.setVerificationCode(null);
        profile.setVerificationCodeExpiration(null);
        profileRepository.save(profile);
        userRepository.save(currentUser);
    }

    // TODO
    @Override
    public void verifyAccount(VerifyAccountRequest req) {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        var profile = profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();
        if (profile.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        if (profile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (!otpService.verifyOtp(req.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        profile.setVerificationCode(null);
        profile.setVerificationCodeExpiration(null);
        profile.setVerified(true);
        profileRepository.save(profile);
    }

    // DONE
    /*@Override
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
                try { cloudinaryService.deleteImageByPublicId(uploadedPublicId); }
                catch (Exception cleanupEx) { log.warn("Cleanup of Cloudinary image failed: {}", cleanupEx.getMessage()); }
            }
            throw ex;
        }
    }*/

    // TODO
    /*@Override
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
    }*/









    // DONE
    /*@Override
    public void sendAccountDeletionCode() {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        var profile =  profileRepository.findById(currentUser.getProfile().getId()).orElseThrow();
        // Generate 6 digit OTP / VERIFICATION CODE
        String otp = otpService.generateOtp();
        System.out.println("GENERATED DELETE OTP: "+otp);
        // Calculate expiration time (current time + 15 minutes in milliseconds)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        profile.setVerificationCode(otpService.encodeOtp(otp));
        profile.setVerificationCodeExpiration(expirationTime);
        profileRepository.save(profile);

        try {
            emailService.sendAccountDeletionCode(currentUser.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send account deletion code to " + currentUser.getEmail(), ex);
        }
    }*/

    // TODO
    /*@Override
    public void sendAccountVerificationCode() {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        if (currentUser.getProfile().isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        String otp = otpService.generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        currentUser.getProfile().setVerificationCode(otpService.encodeOtp(otp));
        currentUser.getProfile().setVerificationCodeExpiration(expirationTime);

        userRepository.save(currentUser);

        try {
            emailService.sendAccountVerificationCode(currentUser.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send account verification code to " + currentUser.getEmail(), ex);
        }
    }*/

    // TODO
    /*@Override
    public void sendPasswordResetCode() {
        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        // Generate 6 digit OTP / VERIFICATION CODE
        String otp = otpService.generateOtp();
        // Calculate expiration time (current time + 15 minutes in milliseconds)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        currentUser.getProfile().setVerificationCode(otpService.encodeOtp(otp));
        currentUser.getProfile().setVerificationCodeExpiration(expirationTime);
        userRepository.save(currentUser);

        try {
            emailService.sendResetPasswordCode(currentUser.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send reset password code to " + currentUser.getEmail(), ex);
        }
    }*/
}
