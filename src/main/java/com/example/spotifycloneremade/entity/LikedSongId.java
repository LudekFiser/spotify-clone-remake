package com.example.spotifycloneremade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongId implements Serializable {

    @Serial
    private static final long serialVersionUID = -2934302966555541284L;

    @Column(name = "user_id")
    private Long userId;


    @Column(name = "song_id")
    private Long songId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LikedSongId entity = (LikedSongId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.songId, entity.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, songId);
    }

}