package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.*;

public interface ProfileService {
    ProfileResponse register(RegisterRequest req);        // USER i ARTIST
    void changePassword(ChangePasswordRequest req);
    void verifyAccount(VerifyAccountRequest req);
    ProfileResponse updateUser(UpdateUserRequest req);      // update profilu
    void deleteUser(DeleteAccountDto otp);
}

