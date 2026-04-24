package com.neofacto.filmhub.api.films.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDetails {
    private Long id;
    private String title;
    private String overview;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;
}