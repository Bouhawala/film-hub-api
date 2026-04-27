package com.neofacto.filmhub.api.auth.service;

import com.neofacto.filmhub.api.auth.dto.AuthResponse;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.dto.RegisterRequest;
import com.neofacto.filmhub.api.auth.dto.UpdateUserRequest;
import com.neofacto.filmhub.api.auth.exception.UsernameAlreadyExistsException;
import com.neofacto.filmhub.api.auth.model.User;
import com.neofacto.filmhub.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
        log.info("Registering new user: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Registration failed - username already taken: {}", request.getUsername());
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("User logged in successfully: {}", user.getUsername());
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public void updateUser(UpdateUserRequest request, String username) {
        log.info("Updating account for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (StringUtils.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.isNotBlank(request.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }
}