package com.example.spotifycloneremade.controller;

import com.example.spotifycloneremade.dto.auth.*;
import com.example.spotifycloneremade.service.ImageService;
import com.example.spotifycloneremade.service.UserEmailService;
import com.example.spotifycloneremade.service.UserService;
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

    private final UserService userService;
    private final UserEmailService userEmailService;
    private final ImageService imageService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
           @Valid @RequestBody RegisterRequest registerRequest,
            UriComponentsBuilder uriBuilder) {
        var user =  userService.register(registerRequest);

        var uri = uriBuilder
                .path("/api/users/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest req) {
        var user = userService.updateUser(req);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<CurrentUserDto> updateProfilePicture(@RequestPart("image") MultipartFile image) {
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
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteAccountDto otp) {
        userService.deleteUser(otp);
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
        userService.verifyAccount(request);
        return ResponseEntity.noContent().build();
    }
}
