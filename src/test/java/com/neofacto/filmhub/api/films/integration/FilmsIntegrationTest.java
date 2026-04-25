package com.neofacto.filmhub.api.films.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neofacto.filmhub.api.WireMockTestBase;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.neofacto.filmhub.api.shared.constants.AppConstants.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmsIntegrationTest extends WireMockTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        userRepository.save(User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password123"))
                .build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password123");

        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldGetAllFilmsPublicly() throws Exception {
        FilmsWireMock.getAllFilms(wireMock);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Venom: The Last Dance"))
                .andExpect(jsonPath("$[1].title").value("Moana 2"));
    }

    @Test
    void shouldGetFilmByIdWithToken() throws Exception {
        FilmsWireMock.getFilmById(wireMock);

        mockMvc.perform(get("/films/912649")
                .header(AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Venom: The Last Dance"))
                .andExpect(jsonPath("$.overview").value("Eddie and Venom are on the run."));
    }

    @Test
    void shouldReturnNotFoundForInvalidFilmId() throws Exception {
        FilmsWireMock.filmNotFound(wireMock);

        mockMvc.perform(get("/films/999999")
                .header(AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Film not found"));
    }

    @Test
    void shouldReturnBadGatewayWhenFilmHubUnavailable() throws Exception {
        FilmsWireMock.filmHubUnavailable(wireMock);

        mockMvc.perform(get("/films"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("FilmHub API internal error"));
    }
}