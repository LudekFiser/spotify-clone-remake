package com.example.spotifycloneremade.utils.otp;

import com.example.spotifycloneremade.dto.auth.VerifyAccountRequest;
import com.example.spotifycloneremade.entity.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public String encodeOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    public boolean verifyOtp(String plainOtp, String encodedOtp) {
        return passwordEncoder.matches(plainOtp, encodedOtp);
    }
}
