package com.example.spotifycloneremade.service;

public interface UserEmailService {

    void sendPasswordResetCode();
    void sendAccountVerificationCode();
    void sendAccountDeletionCode();
}
