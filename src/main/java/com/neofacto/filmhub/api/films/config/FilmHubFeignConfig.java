package com.neofacto.filmhub.api.films.config;

import com.neofacto.filmhub.api.films.exception.decoder.FilmHubErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilmHubFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FilmHubErrorDecoder();
    }
}