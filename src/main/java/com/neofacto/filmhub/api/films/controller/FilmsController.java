package com.neofacto.filmhub.api.films.controller;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.service.FilmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmsController {

    private final FilmsService filmsService;

    @GetMapping
    public ResponseEntity<List<FilmSummary>> getAllFilms() throws FilmHubUnavailableException {
        return ResponseEntity.ok(filmsService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDetails> getFilmById(@PathVariable Long id) throws FilmNotFoundException {
        return ResponseEntity.ok(filmsService.getFilmById(id));
    }
}