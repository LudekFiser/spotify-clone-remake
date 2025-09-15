package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.SongImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongImageRepository extends JpaRepository<SongImage, Long> {
}
