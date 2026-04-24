package com.neofacto.filmhub.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FilmHubApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmHubApiApplication.class, args);
    }

}
