package com.example.spotifycloneremade.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Size(max = 100)
    @Column(name = "genre", length = 100)
    private String genre;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "plays", nullable = false)
    private Long plays;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'SINGLE'")
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "song_image_id")
    private SongImage songImage;

    @OneToMany(mappedBy = "song")
    private Set<SongImage> songImages = new LinkedHashSet<>();

}