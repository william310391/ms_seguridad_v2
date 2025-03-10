package com.ms_seguridad.service;

import com.ms_seguridad.model.request.AuthCreateUserRequest;
import com.ms_seguridad.model.request.AuthLoginRequest;
import com.ms_seguridad.model.response.AuthResponse;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResponse> login(AuthLoginRequest authLoginRequest);
    Mono<AuthResponse> register(AuthCreateUserRequest authCreateUserRequest);
}
