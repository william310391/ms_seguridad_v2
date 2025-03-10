package com.ms_seguridad.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ms_seguridad.model.request.AuthCreateUserRequest;
import com.ms_seguridad.model.request.AuthLoginRequest;
import com.ms_seguridad.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthCreateUserRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

}


// https://chatgpt.com/c/67ca69c6-7dec-800d-abce-a97f042e0f9a