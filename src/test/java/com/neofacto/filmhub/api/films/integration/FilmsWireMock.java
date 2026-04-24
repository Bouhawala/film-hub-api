package com.neofacto.filmhub.api.films.integration;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FilmsWireMock {

    public static final String FILMS_JSON = """
            [
                {"id": 912649, "release_date": "2024-10-22", "title": "Venom: The Last Dance", "vote_average": 6.7},
                {"id": 1241982, "release_date": "2024-11-21", "title": "Moana 2", "vote_average": 6.9}
            ]
            """;

    public static final String FILM_DETAIL_JSON = """
            {
                "id": 912649,
                "original_language": "en",
                "original_title": "Venom: The Last Dance",
                "overview": "Eddie and Venom are on the run.",
                "release_date": "2024-10-22",
                "title": "Venom: The Last Dance",
                "vote_average": 6.7,
                "vote_count": 1315
            }
            """;

    public static void getAllFilms(WireMockServer wireMock) {
        wireMock.stubFor(get(urlEqualTo("/films"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(FILMS_JSON)
                ));
    }

    public static void getFilmById(WireMockServer wireMock) {
        wireMock.stubFor(get(urlEqualTo("/films/912649"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(FILM_DETAIL_JSON)
                ));
    }

    public static void filmNotFound(WireMockServer wireMock) {
        wireMock.stubFor(get(urlEqualTo("/films/999999"))
                .willReturn(aResponse()
                        .withStatus(404)
                ));
    }

    public static void filmHubUnavailable(WireMockServer wireMock) {
        wireMock.stubFor(get(urlEqualTo("/films"))
                .willReturn(aResponse()
                        .withStatus(500)
                ));
    }
}