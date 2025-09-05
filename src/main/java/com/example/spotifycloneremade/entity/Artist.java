package com.example.spotifycloneremade.entity;

import com.example.spotifycloneremade.enums.ROLE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "artists")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    private Long profileId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "plays")
    private Integer plays = 0;

    @Column(name = "num_of_songs")
    private Integer numOfSongs = 0;

    @OneToMany(mappedBy = "artist", fetch = FetchType.EAGER)
    @Builder.Default
    private List<Song> songs = new ArrayList<>();


    /*public Integer calculateNumberOfSongs(List<Song> songs) {
        if (songs != null) {
            return songs.size();
        } else {
            return 0;
        }
    }*/
    public List<Song> getSongs() {
        if (songs == null) songs = new ArrayList<>();
        return songs;
    }
    public int calculateNumberOfSongs() {
        return getSongs().size();
    }


    /*public Integer calculateTotalPlays(List<Song> songs) {
        if (songs != null) {
            return songs.stream()
                    .mapToInt(Song::getPlays)
                    .sum();
        } else {
            return 0;
        }
    }*/
    public int calculateTotalPlays() {
        // pokud máš v Song 'plays' jako int (doporučeno), není potřeba null guard
        return getSongs()
                .stream()
                .mapToInt(Song::getPlays)
                .sum();
    }

    public static boolean isAdult(LocalDate birthDate) {
        return birthDate.plusYears(18).isBefore(LocalDate.now()) ||
                birthDate.plusYears(18).equals(LocalDate.now());
    }

}