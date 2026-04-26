package com.neofacto.filmhub.api.films.exception.decoder;

import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FilmHubErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new FilmNotFoundException("Film not found");
            case 503, 504 -> new RetryableException(
                    response.status(),
                    "Service temporarily unavailable, retrying...",
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
            default -> new FilmHubUnavailableException("FilmHub API is currently unavailable");
        };
    }
}