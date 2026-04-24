package com.neofacto.filmhub.api.films.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.repository.UserRepository;
import com.neofacto.filmhub.api.films.dto.FilmDetails;
import com.neofacto.filmhub.api.films.dto.FilmSummary;
import com.neofacto.filmhub.api.films.exception.FilmHubUnavailableException;
import com.neofacto.filmhub.api.films.exception.FilmNotFoundException;
import com.neofacto.filmhub.api.films.feign.FilmHubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilmsSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FilmHubClient filmHubClient;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        userRepository.save(User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
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
        List<FilmSummary> films = List.of(
                new FilmSummary(1L, "Film 1", "2024-10-22", 7.5f),
                new FilmSummary(2L, "Film 2", "2024-11-01", 6.3f)
        );

        when(filmHubClient.getAllFilms()).thenReturn(films);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Film 1"))
                .andExpect(jsonPath("$[1].title").value("Film 2"));
    }

    @Test
    void shouldGetFilmByIdWithToken() throws Exception {
        FilmDetails film = new FilmDetails(1L, "Film 1", "Overview", "en", "Film 1", "2024-10-22", 7.5f, 1000);

        when(filmHubClient.getFilmById(1L)).thenReturn(film);

        mockMvc.perform(get("/films/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Film 1"))
                .andExpect(jsonPath("$.overview").value("Overview"));
    }

    @Test
    void shouldReturnNotFoundForInvalidFilmId() throws Exception {
        when(filmHubClient.getFilmById(999L)).thenThrow(new FilmNotFoundException("Film not found"));

        mockMvc.perform(get("/films/999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Film not found"));
    }

    @Test
    void shouldReturnBadGatewayWhenFilmHubUnavailable() throws Exception {
        when(filmHubClient.getAllFilms()).thenThrow(new FilmHubUnavailableException("FilmHub API is unavailable"));

        mockMvc.perform(get("/films"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("FilmHub API is unavailable"));
    }
}