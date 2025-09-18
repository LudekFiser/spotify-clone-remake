package com.example.spotifycloneremade.mapper;

import com.example.spotifycloneremade.dto.image.ImageDto;
import com.example.spotifycloneremade.entity.Avatar;
import com.example.spotifycloneremade.entity.ImageSource;
import com.example.spotifycloneremade.entity.Song;
import com.example.spotifycloneremade.entity.SongImage;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDto toImageDto(Avatar a) {
        return a == null ? null : new ImageDto(a.getImageUrl(), a.getPublicId());
    }

    public ImageDto toSongImageDto(SongImage a) {
        return a == null ? null : new ImageDto(a.getImageUrl(), a.getPublicId());
    }

    /*public ImageDto toImageDto(ImageSource src) {
        return src == null ? null : new ImageDto(src.getImageUrl(), src.getPublicId());
    }*/
}


