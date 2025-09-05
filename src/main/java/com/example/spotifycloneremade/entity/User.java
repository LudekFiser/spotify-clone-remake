package com.example.spotifycloneremade.entity;


import com.example.spotifycloneremade.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long profileId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;


    public static boolean isAdult(LocalDate birthDate) {
        return birthDate.plusYears(18).isBefore(LocalDate.now()) ||
               birthDate.plusYears(18).equals(LocalDate.now());
    }
}