package com.neofacto.filmhub.api.auth.unit;

import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    void shouldGenerateToken() {
        UserDetails userDetails = buildUser();
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    void shouldExtractUsername() {
        UserDetails userDetails = buildUser();
        String token = jwtService.generateToken(userDetails);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateToken() {
        UserDetails userDetails = buildUser();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldInvalidateTokenForWrongUser() {
        UserDetails userDetails = buildUser();
        UserDetails otherUser = User.builder()
                .username("otheruser")
                .password("password")
                .build();
        String token = jwtService.generateToken(userDetails);
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    private User buildUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .build();
    }
}