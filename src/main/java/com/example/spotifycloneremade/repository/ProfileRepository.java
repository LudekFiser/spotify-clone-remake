package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.dto.auth.SearchProfileResponse;
import com.example.spotifycloneremade.entity.Profile;
import com.example.spotifycloneremade.entity.Song;
import com.example.spotifycloneremade.entity.User;
import com.example.spotifycloneremade.enums.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByEmail(String email);
    Optional<Profile> findByEmail(String email);

    @Query("SELECT p FROM Profile p WHERE (:role IS NULL OR p.role = :role)")
    List<Profile> findByRoleOnly(@Param("role") ROLE role);

    /*@Query("SELECT p FROM Profile p WHERE (:role IS NULL OR p.role = :role) AND p.name ILIKE CONCAT('%', :name, '%')")
    List<Profile> findByRoleAndName(@Param("role") ROLE role, @Param("name") String name);*/


    /*@Query("""
    SELECT DISTINCT p FROM Profile p
    LEFT JOIN p.artist a
    LEFT JOIN a.songs s
    WHERE (:role IS NULL OR p.role = :role)
      AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
      AND (:songName IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :songName, '%')))
""")
    List<Profile> findByRoleAndNameAndSong(@Param("role") ROLE role,
                                           @Param("name") String name,
                                           @Param("songName") String songName);*/
    @Query("""
    SELECT DISTINCT p FROM Profile p
    LEFT JOIN p.artist a
    LEFT JOIN a.songs s
    WHERE (:role IS NULL OR p.role = :role)
      AND (:artistName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :artistName, '%')))
      AND (:songName IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :songName, '%')))
""")
    List<Profile> findByRoleAndNameAndSong(@Param("role") ROLE role,
                                           @Param("artistName") String artistName,
                                           @Param("songName") String songName);


    @Query("""
    SELECT p FROM Profile p
    WHERE (:role IS NULL OR p.role = :role)
      AND (:artistName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :artistName, '%')))
""")
    List<Profile> findByRoleAndName(@Param("role") ROLE role, @Param("artistName") String artistName);







}
