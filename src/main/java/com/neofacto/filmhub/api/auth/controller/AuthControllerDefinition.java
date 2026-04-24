package com.neofacto.filmhub.api.auth.controller;

import com.neofacto.filmhub.api.auth.dto.AuthResponse;
import com.neofacto.filmhub.api.auth.dto.LoginRequest;
import com.neofacto.filmhub.api.auth.dto.RegisterRequest;
import com.neofacto.filmhub.api.auth.exception.UsernameAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "Auth endpoints")
public interface AuthControllerDefinition {

    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username already taken"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws UsernameAlreadyExistsException;

    @Operation(summary = "Login with username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);
}