package com.neofacto.filmhub.api.films.exception;

import com.neofacto.filmhub.api.shared.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FilmsExceptionHandler {

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FilmNotFoundException exception) {
        log.error("Film not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(FilmHubUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(FilmHubUnavailableException exception) {
        log.error("FilmHub API unavailable: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(exception.getMessage()));
    }
}