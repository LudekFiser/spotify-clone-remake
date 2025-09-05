package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByEmail(String email);
    Optional<Profile> findByEmail(String email);
}
