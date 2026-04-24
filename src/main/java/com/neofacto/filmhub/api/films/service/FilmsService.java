package com.neofacto.filmhub.api.films.service;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.feign.FilmHubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmsService {

    private final FilmHubClient filmHubClient;

    public List<FilmSummary> getAllFilms() throws FilmHubUnavailableException {
        return filmHubClient.getAllFilms();
    }

    public FilmDetails getFilmById(Long id) throws FilmNotFoundException {
        return filmHubClient.getFilmById(id);
    }
}