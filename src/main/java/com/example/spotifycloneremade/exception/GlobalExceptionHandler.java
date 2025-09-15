package com.example.spotifycloneremade.exception;


import com.example.spotifycloneremade.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleUnreadableMessage() {
        return ResponseEntity.badRequest().body(
                new ErrorDto("Invalid request body")
        );
    }

    // 400 – @Valid na body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /*@ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }*/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto>handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Email is already registered!")
        );
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorDto("Invalid email or password")
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto("User not found!")
        );
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<ErrorDto> handlePasswordsDoNotMatchException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Old Password does not match!")
        );
    }

    @ExceptionHandler(PasswordsMatchingException.class)
    public ResponseEntity<ErrorDto> handlePasswordsMatchingException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("New password cannot be the same as the Old password!")
        );
    }

    @ExceptionHandler(PasswordResetReqNotMatching.class)
    public ResponseEntity<ErrorDto> handlePasswordResetReqNotMatchingException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Passwords do not Match!")
        );
    }

    @ExceptionHandler(NotOldEnoughException.class)
    public ResponseEntity<ErrorDto> handleNotOldEnoughException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("You must be 18+ years old to proceed!")
        );
    }


    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<ErrorDto> handleTooManyAttemptsException(TooManyAttemptsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                new ErrorDto(ex.getMessage())
        );
    }


}

