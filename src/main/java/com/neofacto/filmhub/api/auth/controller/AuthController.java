package com.neofacto.filmhub.api.auth.controller;

import com.neofacto.filmhub.api.auth.dto.*;
import com.neofacto.filmhub.api.auth.exception.UsernameAlreadyExistsException;
import com.neofacto.filmhub.api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthControllerDefinition {

    private final AuthService authService;

    public ResponseEntity<AuthResponse> register(RegisterRequest request) throws UsernameAlreadyExistsException {
        log.info("POST /auth/register - username: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        log.info("POST /auth/login - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    public ResponseEntity<UpdateUserResponse> updateUser(UpdateUserRequest request) {
        log.info("PUT /auth/account - user: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUser(request, username);
        return ResponseEntity.ok(new UpdateUserResponse("User updated successfully"));
    }
}