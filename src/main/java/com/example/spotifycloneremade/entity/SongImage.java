package com.example.spotifycloneremade.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "song_images")
public class SongImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
    private String imageUrl;

    @NotNull
    @Column(name = "public_id", nullable = false, length = Integer.MAX_VALUE)
    private String publicId;

    @NotNull
    @Column(name = "ord", nullable = false)
    private Integer ord;

    @Size(max = 50)
    @NotNull
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @OneToMany(mappedBy = "songImage")
    private Set<Song> songs = new LinkedHashSet<>();

}