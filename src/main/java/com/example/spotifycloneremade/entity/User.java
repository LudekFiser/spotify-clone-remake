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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ROLE role;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth", updatable = false/*, insertable = false*/)
    private LocalDate dateOfBirth;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private Profile profile;


    public static boolean isAdult(LocalDate birthDate) {
        return birthDate.plusYears(18).isBefore(LocalDate.now()) ||
               birthDate.plusYears(18).equals(LocalDate.now());
    }
}