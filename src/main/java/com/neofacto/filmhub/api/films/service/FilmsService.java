package com.neofacto.filmhub.api.films.service;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.feign.FilmHubClient;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.neofacto.filmhub.api.shared.constants.AppConstants.FILMS;
import static com.neofacto.filmhub.api.shared.constants.AppConstants.FILM_DETAILS;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmsService {

    private final FilmHubClient filmHubClient;

    @RateLimiter(name = FILMS)
    @Cacheable(FILMS)
    public List<FilmSummary> getAllFilms() throws FilmHubUnavailableException {
        log.info("Fetching all films from FilmHub API");
        return filmHubClient.getAllFilms();
    }

    @RateLimiter(name = FILMS)
    @Cacheable(value = FILM_DETAILS, key = "#id")
    public FilmDetails getFilmById(Long id) throws FilmNotFoundException {
        log.info("Fetching film with id: {}", id);
        return filmHubClient.getFilmById(id);
    }

    public List<FilmSummary> getAllFilmsFallback(Throwable t) throws FilmHubUnavailableException {
        log.warn("Circuit breaker triggered for getAllFilms: {}", t.getMessage());
        throw new FilmHubUnavailableException("FilmHub API is currently unavailable");
    }
    public FilmDetails getFilmByIdFallback(Long id, FilmNotFoundException e) throws FilmNotFoundException {
        throw e;
    }

    public FilmDetails getFilmByIdFallback(Long id, Throwable t) throws FilmHubUnavailableException {
        log.warn("Circuit breaker triggered for getFilmById {}: {}", id, t.getMessage());
        throw new FilmHubUnavailableException("FilmHub API is currently unavailable");
    }
}