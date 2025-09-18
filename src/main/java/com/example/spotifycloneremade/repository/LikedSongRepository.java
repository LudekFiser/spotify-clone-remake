package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.LikedSong;
import com.example.spotifycloneremade.entity.LikedSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, Long> {


    @Query("SELECT ls FROM LikedSong ls WHERE ls.user.profileId = :profileId")
    List<LikedSong> findByUserProfileId(@Param("profileId") Long profileId);

    boolean existsById(LikedSongId id);

    void deleteById(LikedSongId id);
}
