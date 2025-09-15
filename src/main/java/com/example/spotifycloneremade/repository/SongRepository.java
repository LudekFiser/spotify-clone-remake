package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.Artist;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.Song;
import com.example.spotifycloneremade.enums.ROLE;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    /*@Query("SELECT s FROM Song s WHERE  s.title ILIKE CONCAT('%', :title, '%')")
    List<Song> findByTitle(@Param("title") String title);*/
    @EntityGraph(attributePaths = {"artist.profile", "songImage"})
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Song> findByTitleWithArtistAndImage(@Param("title") String title);



    @Query("SELECT s FROM Song s WHERE s.artist.profile.name = :name")
    List<Song> findByArtist(@Param("name") String artistName);
}
