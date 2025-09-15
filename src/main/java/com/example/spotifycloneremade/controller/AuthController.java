package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.jwt.JwtConfig;
import com.example.spotifycloneremade.jwt.JwtResponse;
import com.example.spotifycloneremade.jwt.JwtService;
import com.example.spotifycloneremade.mapper.ProfileMapper;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.service.EmailService;
import com.example.spotifycloneremade.utils.otp.OtpService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
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
    private final OtpService otpService;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Profile profile = profileRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.isTwoFactorEmail()) {
            String code = otpService.generateOtp();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            profile.setVerificationCode(otpService.encodeOtp(code));
            profile.setVerificationCodeExpiration(expirationTime);
            profileRepository.save(profile);

            // pošli e-mail (sync/async dle tvé volby)
            emailService.send2FAVerificationCode(profile.getEmail(), code);

            // short-lived 2FA token
            String twoFaToken = jwtService.generateTwoFaToken(profile.getId(), Duration.ofMinutes(5));
            Cookie c = new Cookie("twoFaToken", twoFaToken);
            c.setHttpOnly(true);
            c.setSecure(true);
            c.setPath("/auth/verify-2fa");   // cookie pošle jen na verify endpoint
            c.setMaxAge(5 * 60);
            response.addCookie(c);

            return ResponseEntity.ok(new TwoFAResponse("2FA_REQUIRED"));
        }

        // bez 2FA rovnou tokeny
        return issueTokens(response, profile);
    }

    // VERIFY 2FA
    @PostMapping("/verify-2fa")
    public ResponseEntity<JwtResponse> verify2fa(
            @Valid @RequestBody TwoFARequest req,
            @CookieValue(value = "twoFaToken", required = false) String twoFaCookie,
            @RequestHeader(value = "X-2FA-Token", required = false) String twoFaHeader,
            HttpServletResponse response
    ) {
        // 1) získat token (cookie má prioritu)
        //String twoFaToken = (twoFaCookie != null) ? twoFaCookie : twoFaHeader;
        String twoFaToken;
        if (twoFaCookie != null) {
            twoFaToken = twoFaCookie;
        } else {
            twoFaToken = twoFaHeader;
        }

        if (twoFaToken == null || twoFaToken.isBlank()) {
            throw new RuntimeException("Missing 2FA token");
        }

        // 2) vyčíst profileId a zkontrolovat scope/exp
        Long profileId = jwtService.parseTwoFaToken(twoFaToken);

        // 3) ověřit OTP
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getVerificationCode() == null ||
                profile.getVerificationCodeExpiration() == null ||
                profile.getVerificationCodeExpiration().isBefore(LocalDateTime.now()) ||
                !otpService.verifyOtp(req.getOtp(), profile.getVerificationCode())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // 4) reset OTP + zrušit 2FA cookie
        profile.setVerificationCode(null);
        profile.setVerificationCodeExpiration(null);
        profileRepository.save(profile);

        Cookie clear = new Cookie("twoFaToken", "");
        clear.setHttpOnly(true);
        clear.setSecure(true);
        clear.setPath("/auth/verify-2fa");
        clear.setMaxAge(0);
        response.addCookie(clear);

        // 5) vydat běžné tokeny
        return issueTokens(response, profile);
    }


    // /me – vrátí sjednocený ProfileResponse
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var profileId = (Long) auth.getPrincipal();
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(profileMapper.toResponse(profile));
    }


    // REFRESH – čte refresh token z cookie, vrací nový access token
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Profile profile = profileRepository.findById(jwt.getProfileId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        var accessToken = jwtService.generateAccessToken(profile);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    /* ---------- private helpers ---------- */

    /*private ResponseEntity<JwtResponse> issueTokens(HttpServletResponse response, Profile profile) {
        var accessToken = jwtService.generateAccessToken(profile);
        var refreshToken = jwtService.generateRefreshToken(profile);

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }*/
    private ResponseEntity<JwtResponse> issueTokens(HttpServletResponse response, Profile profile) {
        var accessToken  = jwtService.generateAccessToken(profile);
        var refreshToken = jwtService.generateRefreshToken(profile);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.toString())
                .httpOnly(true)
                .secure(true)             // vyžaduje HTTPS / nebo localhost je povolen
                .sameSite("None")         // ← důležité pro cross-site
                .path("/auth/refresh")    // ← budeš tím pádem mazat se stejným path
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

}
