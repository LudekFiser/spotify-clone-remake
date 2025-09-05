package com.example.spotifycloneremade.jwt;

import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public Jwt generateAccessToken(Profile profile) {
        return generateToken(profile, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(Profile profile) {
        return generateToken(profile, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(Profile profile, long tokenExpirationSec) {
        var claims = Jwts.claims()
                .subject(profile.getId().toString())
                .add("email", profile.getEmail())
                .add("name",  profile.getName())
                .add("role",  profile.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * tokenExpirationSec))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (JwtException e) {
            return null;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }






    // === 2FA token (krátkodobý, jen pro ověření OTP) ===
    public String generateTwoFaToken(long profileId, Duration ttl) {
        var claims = Jwts.claims()
                .subject(Long.toString(profileId))
                .add("twofa", true)            // účel tokenu
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public Long parseTwoFaToken(String token) {
        try {
            var parsed = Jwts.parser()
                    .verifyWith(jwtConfig.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // zkontroluj účel
            Boolean isTwoFa = parsed.get("twofa", Boolean.class);
            if (isTwoFa == null || !isTwoFa) return null;

            return Long.valueOf(parsed.getSubject()); // profileId
        } catch (Exception e) {
            return null;
        }
    }


}
