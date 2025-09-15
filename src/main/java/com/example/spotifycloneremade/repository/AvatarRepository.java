package com.example.spotifycloneremade.repository;

import com.example.spotifycloneremade.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
