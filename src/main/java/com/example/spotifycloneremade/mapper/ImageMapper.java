package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDto toImageDto(Avatar a) {
        return a == null ? null : new ImageDto(a.getImageUrl(), a.getPublicId());
    }
}
