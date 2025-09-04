package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    /*public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }*/
    public User getCurrentUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            var userId = (Long) authentication.getPrincipal();
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            return null; // If Something is wrong with the token
        }
    }
}
