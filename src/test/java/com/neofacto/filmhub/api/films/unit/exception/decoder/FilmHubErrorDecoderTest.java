package com.neofacto.filmhub.api.films.unit.exception.decoder;

import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.exception.decoder.FilmHubErrorDecoder;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class FilmHubErrorDecoderTest {

    private FilmHubErrorDecoder errorDecoder;

    @BeforeEach
    void setUp() {
        errorDecoder = new FilmHubErrorDecoder();
    }

    @Test
    void shouldReturnFilmNotFoundExceptionOn404() {
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(Request.create(Request.HttpMethod.GET, "/films/1", Map.of(), null, null, null))
                .headers(Map.of())
                .build();

        Exception exception = errorDecoder.decode("getAllFilms", response);

        assertInstanceOf(FilmNotFoundException.class, exception);
        assertEquals("Film not found", exception.getMessage());
    }

    @Test
    void shouldReturnFilmHubUnavailableExceptionOn500() {
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(Request.create(Request.HttpMethod.GET, "/films", Map.of(), null, null, null))
                .headers(Map.of())
                .build();

        Exception exception = errorDecoder.decode("getAllFilms", response);

        assertInstanceOf(FilmHubUnavailableException.class, exception);
        assertEquals("FilmHub API internal error", exception.getMessage());
    }

    @Test
    void shouldReturnRetryableExceptionOn503() {
        Response response = Response.builder()
                .status(503)
                .reason("Service Unavailable")
                .request(Request.create(Request.HttpMethod.GET, "/films", Map.of(), null, null, null))
                .headers(Map.of())
                .build();

        Exception exception = errorDecoder.decode("getAllFilms", response);

        assertInstanceOf(RetryableException.class, exception);
    }

    @Test
    void shouldReturnRetryableExceptionOn504() {
        Response response = Response.builder()
                .status(504)
                .reason("Gateway Timeout")
                .request(Request.create(Request.HttpMethod.GET, "/films", Map.of(), null, null, null))
                .headers(Map.of())
                .build();

        Exception exception = errorDecoder.decode("getAllFilms", response);

        assertInstanceOf(RetryableException.class, exception);
    }

    @Test
    void shouldReturnFilmHubUnavailableExceptionOnDefault() {
        Response response = Response.builder()
                .status(502)
                .reason("Bad Gateway")
                .request(Request.create(Request.HttpMethod.GET, "/films", Map.of(), null, null, null))
                .headers(Map.of())
                .build();

        Exception exception = errorDecoder.decode("getAllFilms", response);

        assertInstanceOf(FilmHubUnavailableException.class, exception);
        assertEquals("FilmHub API is currently unavailable", exception.getMessage());
    }
}