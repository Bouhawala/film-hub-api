package com.neofacto.filmhub.api.auth.unit;

import com.neofacto.filmhub.api.auth.dto.AuthResponse;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.dto.RegisterRequest;
import com.neofacto.filmhub.api.auth.dto.UpdateUserRequest;
import com.neofacto.filmhub.api.auth.exception.UsernameAlreadyExistsException;
import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.repository.UserRepository;
import com.neofacto.filmhub.api.auth.service.AuthService;
import com.neofacto.filmhub.api.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterSuccessfully() throws UsernameAlreadyExistsException {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        User user = User.builder()
                .username("testUser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("password123");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowWhenBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("wrongPassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@email.com");
        request.setNewPassword("newPassword123");

        User user = User.builder()
                .username("testUser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        authService.updateUser(request, "testUser");

        verify(userRepository).save(any(User.class));
        assertEquals("new@email.com", user.getEmail());
        assertEquals("newEncodedPassword", user.getPassword());
    }
}