package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.service.ImageService;
import com.example.spotifycloneremade.service.ProfileService;
import com.example.spotifycloneremade.service.UserEmailService;
import com.example.spotifycloneremade.utils.rateLimit.RateLimit;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "users")
public class UserController {

    private final ProfileService profileService;
    private final UserEmailService userEmailService;
    private final ImageService imageService;


    @PostMapping("/register")
    public ResponseEntity<ProfileResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            UriComponentsBuilder uriBuilder) {
        var user = profileService.register(registerRequest);

        var uri = uriBuilder
                .path("/api/users/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(uri).body(user);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest req) {
        var user = profileService.updateUser(req);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<ProfileResponse> updateProfilePicture(@RequestPart("image") MultipartFile image) {
        var changeProfilePicture = imageService.changeProfilePicture(image);
        return ResponseEntity.ok().body(changeProfilePicture);
    }

    @DeleteMapping("/profile-picture/delete")
    public ResponseEntity<Void> deleteProfilePicture() {
        imageService.deleteProfilePicture();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        profileService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteAccountDto otp) {
        profileService.deleteUser(otp);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-account-delete-code")
    public ResponseEntity<Void> sendAccountDeletionCode() {
        userEmailService.sendAccountDeletionCode();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-password-reset-code")  // might also be forgot-password endpoint
    public ResponseEntity<Void> sendPasswordResetCode() {
        //userService.sendPasswordResetCode();
        userEmailService.sendPasswordResetCode();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-account-verification-code")
    @RateLimit(requests = 3, timeAmount = 1, timeUnit = TimeUnit.HOURS, keyPrefix = "send-verification-code")
    public ResponseEntity<Void> sendAccountVerificationCode() {
        userEmailService.sendAccountVerificationCode();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-account")
    public ResponseEntity<Void> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        profileService.verifyAccount(request);
        return ResponseEntity.noContent().build();
    }
}
