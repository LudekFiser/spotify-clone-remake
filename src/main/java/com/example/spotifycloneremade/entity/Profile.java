package com.example.spotifycloneremade.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "two_factor_email")
    @Builder.Default
    private Boolean twoFactorEmail = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expiration")
    private LocalDateTime verificationCodeExpiration;


    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;

    //@OneToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;


}