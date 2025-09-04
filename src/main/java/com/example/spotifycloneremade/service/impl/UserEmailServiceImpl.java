package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.exception.UserNotFoundException;
import com.example.spotifycloneremade.mapper.UserMapper;
import com.example.spotifycloneremade.repository.AvatarRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.service.UserEmailService;
import com.example.spotifycloneremade.utils.cloudinary.CloudinaryService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UserEmailServiceImpl implements UserEmailService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuthService authService;
    private final OtpService otpService;
    private final ProfileRepository profileRepository;

    @Override
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
    }

    @Override
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
    }

    @Override
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
    }
}
