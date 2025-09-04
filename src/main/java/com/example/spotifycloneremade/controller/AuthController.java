package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.CurrentUserDto;
import com.example.spotifycloneremade.dto.auth.LoginRequest;
import com.example.spotifycloneremade.dto.auth.TwoFARequest;
import com.example.spotifycloneremade.dto.auth.TwoFAResponse;
import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.jwt.JwtConfig;
import com.example.spotifycloneremade.jwt.JwtResponse;
import com.example.spotifycloneremade.jwt.JwtService;
import com.example.spotifycloneremade.mapper.UserMapper;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OtpService otpService;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var profile = profileRepository.findById(user.getId()).orElseThrow();

        if(profile.getTwoFactorEmail()) {
            try {
                String code = otpService.generateOtp();
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
                profile.setVerificationCode(otpService.encodeOtp(code));
                profile.setVerificationCodeExpiration(expirationTime);
                profileRepository.save(profile);

                emailService.send2FAVerificationCode(user.getEmail(), code);

                return ResponseEntity.ok(new TwoFAResponse(
                        "2FA Required",
                        user.getId()
                ));
            } catch (Exception e) {
                log.error("Failed to send 2FA code to user: {}", user.getEmail(), e);
                profile.setVerificationCode(null);
                profile.setVerificationCodeExpiration(null);

                profileRepository.save(profile);

                throw new RuntimeException("Failed to send 2FA verification code. Please try again.");
            }
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);


        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());  // 7 days
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<JwtResponse> verify2fa(
            @Valid @RequestBody TwoFARequest req,
            HttpServletResponse response
    ) {
        var user = userRepository.findById(req.getUserId()).orElseThrow();
        var profile = profileRepository.findById(user.getId()).orElseThrow();

        if (profile.getVerificationCode() == null) {
            throw new RuntimeException("Invalid OTP");
        }
        if (profile.getVerificationCodeExpiration() == null ||
            profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        if (!otpService.verifyOtp(req.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Reset 2FA State
        profile.setVerificationCode(null);
        profile.setVerificationCodeExpiration(null);
        profileRepository.save(profile);


        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);


        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());  // 7 days
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toUserDto(user);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }


    // helper method might use?

    private ResponseEntity<JwtResponse> getResponseEntity(HttpServletResponse response, User user) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);


        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());  // 7 days
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
}
