package com.example.spotifycloneremade.entity;

import com.example.spotifycloneremade.enums.ROLE;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
@Setter
@Entity
@Table(name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

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

    @Column(name = "date_of_birth", updatable = false/*, insertable = false*/)
    private LocalDate dateOfBirth;


    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "two_factor_email")
    @Builder.Default
    private boolean twoFactorEmail = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expiration")
    private LocalDateTime verificationCodeExpiration;


    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    //@OneToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private User user;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private Artist artist;


    public static boolean isAdult(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears() >= 18;
    }
}