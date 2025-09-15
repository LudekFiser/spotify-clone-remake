package com.example.spotifycloneremade.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultResponse {
    private List<SearchProfileResponse> profiles;
    private List<SongSummaryDto> songs;
}

