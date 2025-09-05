package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.repository.ArtistRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final ProfileRepository profileRepository;

    /*public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }*/
    public Profile getCurrentProfile() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object p = auth.getPrincipal();
        Long profileId = (p instanceof Long l) ? l :
                (p instanceof String s) ? Long.valueOf(s) :
                        (p instanceof Integer i) ? i.longValue() : null;
        if (profileId == null) return null;

        return profileRepository.findById(profileId).orElse(null);
    }


}
