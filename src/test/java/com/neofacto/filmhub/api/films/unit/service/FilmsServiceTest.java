package com.neofacto.filmhub.api.films.unit.service;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.feign.FilmHubClient;
import com.neofacto.filmhub.api.films.service.FilmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmsServiceTest {

    @Mock
    private FilmHubClient filmHubClient;

    @InjectMocks
    private FilmsService filmsService;

    @Test
    void shouldGetAllFilms() throws FilmHubUnavailableException {
        List<FilmSummary> films = List.of(
                new FilmSummary(1L, "Film 1", "2024-10-22", 7.5f),
                new FilmSummary(2L, "Film 2", "2024-11-01", 6.3f)
        );

        when(filmHubClient.getAllFilms()).thenReturn(films);

        List<FilmSummary> result = filmsService.getAllFilms();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(filmHubClient).getAllFilms();
    }

    @Test
    void shouldGetFilmById() throws FilmNotFoundException {
        FilmDetails film = new FilmDetails(1L, "Film 1", "Overview", "en", "Film 1", "2024-10-22", 7.5f, 1000);

        when(filmHubClient.getFilmById(1L)).thenReturn(film);

        FilmDetails result = filmsService.getFilmById(1L);

        assertNotNull(result);
        assertEquals("Film 1", result.getTitle());
        verify(filmHubClient).getFilmById(1L);
    }

    @Test
    void shouldThrowFilmNotFoundException() throws FilmNotFoundException {
        when(filmHubClient.getFilmById(999L)).thenThrow(new FilmNotFoundException("Film not found"));

        assertThrows(FilmNotFoundException.class, () -> filmsService.getFilmById(999L));
    }

    @Test
    void shouldThrowFilmHubUnavailableException() throws FilmHubUnavailableException {
        when(filmHubClient.getAllFilms()).thenThrow(new FilmHubUnavailableException("FilmHub API is currently unavailable"));

        assertThrows(FilmHubUnavailableException.class, () -> filmsService.getAllFilms());
    }
}