package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
