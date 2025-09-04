package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.dto.auth.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    RegisterResponse register(RegisterRequest req);

    //void sendPasswordResetCode();
    void changePassword(ChangePasswordRequest req);

    //void sendAccountVerificationCode();
    void verifyAccount(VerifyAccountRequest req);


    RegisterResponse updateUser(UpdateUserRequest req);
    //CurrentUserDto changeProfilePicture(MultipartFile image);

    //void sendAccountDeletionCode();
    void deleteUser(DeleteAccountDto otp);
    //void deleteProfilePicture();
}
