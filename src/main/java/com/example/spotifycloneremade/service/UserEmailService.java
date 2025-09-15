package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.resetPassword.SendForgotPasswordRequest;

public interface UserEmailService {

    void sendPasswordResetCode();
    void sendAccountVerificationCode();
    void sendAccountDeletionCode();
    void sendForgotPasswordCode(SendForgotPasswordRequest req);
}
