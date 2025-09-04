package com.example.spotifycloneremade.dto.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedImageDto {
    private String url;
    private String publicId;
}
