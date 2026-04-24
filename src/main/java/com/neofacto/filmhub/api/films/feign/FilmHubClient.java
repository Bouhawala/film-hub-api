package com.neofacto.filmhub.api.films.feign;

import com.neofacto.filmhub.api.films.config.FilmHubFeignConfig;
import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "filmHubClient", url = "${filmhub.base-url}", configuration = FilmHubFeignConfig.class)
public interface FilmHubClient {

    @GetMapping("/films")
    List<FilmSummary> getAllFilms() throws FilmHubUnavailableException;

    @GetMapping("/films/{id}")
    FilmDetails getFilmById(@PathVariable Long id) throws FilmNotFoundException;
}