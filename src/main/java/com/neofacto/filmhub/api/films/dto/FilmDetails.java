package com.neofacto.filmhub.api.films.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FilmDetails {
    private Long id;
    private String title;
    private String overview;
    private String originalLanguage;
    private String originalTitle;
    private String releaseDate;
    private Float voteAverage;
    private Integer voteCount;
}