package com.neofacto.filmhub.api.auth.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.dto.RegisterRequest;
import com.neofacto.filmhub.api.auth.dto.UpdateUserRequest;
import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.neofacto.filmhub.api.shared.constants.AppConstants.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnConflictWhenUsernameExists() throws Exception {
        userRepository.save(User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password123"))
                .build());

        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        userRepository.save(User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password123"))
                .build());

        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenBadCredentials() throws Exception {
        userRepository.save(User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password123"))
                .build());

        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("123");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser("testUser")
    void shouldUpdateUserWithToken() throws Exception {
        userRepository.save(User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password123"))
                .build());

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("testUser", "password123"))))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@email.com");
        request.setNewPassword("newPassword123");

        mockMvc.perform(put("/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, BEARER_PREFIX + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}