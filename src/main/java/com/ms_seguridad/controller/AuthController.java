package com.ms_seguridad.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.core.Authentication;

import com.ms_seguridad.model.request.AuthLoginRequest;
import com.ms_seguridad.service.MyReactiveUserDetailsService;

// import com.ms_seguridad.model.request.AuthRequest;
// import com.ms_seguridad.util.JwtUtil;

// import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private MyReactiveUserDetailsService userDetailService;


    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest userRequest) {
        return new ResponseEntity<>(userDetailService.login(userRequest), HttpStatus.OK);
    }

}


// https://chatgpt.com/c/67ca69c6-7dec-800d-abce-a97f042e0f9a