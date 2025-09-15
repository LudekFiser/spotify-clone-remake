package com.example.spotifycloneremade.service.impl;

import com.example.spotifycloneremade.dto.auth.resetPassword.SendForgotPasswordRequest;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.exception.UserNotFoundException;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.AuthService;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.service.UserEmailService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Profile me = authService.getCurrentProfile();

        // vygeneruj OTP
        String otp = otpService.generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        me.setVerificationCode(otpService.encodeOtp(otp));
        me.setVerificationCodeExpiration(expirationTime);
        profileRepository.save(me);

        try {
            emailService.sendResetPasswordCode(me.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send reset password code to " + me.getEmail(), ex);
        }
    }

    @Override
    public void sendAccountVerificationCode() {
        Profile me = authService.getCurrentProfile();

        if (me.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        String otp = otpService.generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        me.setVerificationCode(otpService.encodeOtp(otp));
        me.setVerificationCodeExpiration(expirationTime);
        profileRepository.save(me);

        try {
            emailService.sendAccountVerificationCode(me.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send account verification code to " + me.getEmail(), ex);
        }
    }

    @Override
    public void sendAccountDeletionCode() {
        Profile me = authService.getCurrentProfile();

        String otp = otpService.generateOtp();
        System.out.println("GENERATED DELETE OTP: " + otp);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        me.setVerificationCode(otpService.encodeOtp(otp));
        me.setVerificationCodeExpiration(expirationTime);
        profileRepository.save(me);

        try {
            emailService.sendAccountDeletionCode(me.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send account deletion code to " + me.getEmail(), ex);
        }
    }

    @Override
    public void sendForgotPasswordCode(SendForgotPasswordRequest req) {
        var profile = profileRepository.findByEmail(req.getEmail())
                .orElseThrow(UserNotFoundException::new);

        // vygeneruj OTP
        String otp = otpService.generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        // ulož OTP k profilu
        profile.setVerificationCode(otpService.encodeOtp(otp));
        profile.setVerificationCodeExpiration(expirationTime);
        profileRepository.save(profile);

        try {
            emailService.sendForgotPasswordCode(req.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send forgot password code to " + req.getEmail(), ex);
        }
    }


}
