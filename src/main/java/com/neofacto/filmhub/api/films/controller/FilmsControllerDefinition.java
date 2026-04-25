package com.neofacto.filmhub.api.films.controller;

import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.neofacto.filmhub.api.shared.constants.AppConstants.FILMS;

@Tag(name = FILMS, description = "Films endpoints")
public interface FilmsControllerDefinition {

    @Operation(summary = "Get all films", description = "Public endpoint, no authentication required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of films"),
            @ApiResponse(responseCode = "502", description = "FilmHub API unavailable")
    })
    @GetMapping
    ResponseEntity<List<FilmSummary>> getAllFilms() throws FilmHubUnavailableException;

    @Operation(summary = "Get film by id", description = "Protected endpoint, requires JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Film details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Film not found"),
            @ApiResponse(responseCode = "502", description = "FilmHub API unavailable")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}")
    ResponseEntity<FilmDetails> getFilmById(@PathVariable Long id) throws FilmNotFoundException;
}