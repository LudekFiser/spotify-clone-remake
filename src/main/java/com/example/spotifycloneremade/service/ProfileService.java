package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.dto.auth.resetPassword.ResetPasswordRequest;
import com.example.spotifycloneremade.enums.ROLE;

import java.util.List;

public interface ProfileService {
    ProfileResponse register(RegisterRequest req);        // USER i ARTIST
    ProfileResponse updateUser(UpdateUserRequest req);

    void deleteUser(DeleteAccountDto otp);
    void changePassword(ChangePasswordRequest req);
    void verifyAccount(VerifyAccountRequest req);
    void forgotPassword(ResetPasswordRequest req);


    /*List<SearchProfileResponse> findAllProfiles();

    List<SearchProfileResponse> findAllArtists();
    List<SearchProfileResponse> findAllUsers();*/

    //List<SearchProfileResponse> searchProfiles(ROLE role, String name, String songName);
    SearchResultResponse searchProfiles(ROLE role, String artistName, String songName);
    SearchProfileResponse findByProfileId(Long id);
}

