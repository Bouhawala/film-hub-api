package com.neofacto.filmhub.api.films.controller;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.service.FilmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmsController implements FilmsControllerDefinition {

    private final FilmsService filmsService;

    public ResponseEntity<List<FilmSummary>> getAllFilms() throws FilmHubUnavailableException {
        log.info("Fetching all films");
        return ResponseEntity.ok(filmsService.getAllFilms());
    }

    public ResponseEntity<FilmDetails> getFilmById(Long id) throws FilmNotFoundException {
        log.info("Fetching film with id: {}", id);
        return ResponseEntity.ok(filmsService.getFilmById(id));
    }
}