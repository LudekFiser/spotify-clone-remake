package com.example.spotifycloneremade.service;

import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.repository.ArtistRepository;
import com.example.spotifycloneremade.repository.ProfileRepository;
import com.example.spotifycloneremade.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + profile.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                profile.getEmail(),
                profile.getPassword(),
                authorities
        );
    }





    /*@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: "+email));

        if (user != null) {

        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // AUTHORITIES
        );
    }*/
}
