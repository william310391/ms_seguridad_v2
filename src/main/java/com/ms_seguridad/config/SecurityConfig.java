package com.ms_seguridad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.ms_seguridad.config.filter.JwtFilter;
import com.ms_seguridad.service.Impl.ReactiveUserDetailsServiceImpl;

import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {
    
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authManager,JwtFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) 
                .authorizeExchange(auth -> {
                    auth.pathMatchers("/auth/**").permitAll();
                    auth.pathMatchers("/prueba/data").hasRole("ADMIN"); //Authority("ADMIN"); // Solo ADMIN puede acceder
                    auth.anyExchange().authenticated(); // Requiere autenticación para cualquier otra ruta
                })
                .authenticationManager(authentication -> Mono.empty()) // Desactiva autenticación predeterminada
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION) // Agregar el filtro JWT
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsServiceImpl userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder()); // Aquí se inyecta el encoder
        return authenticationManager;
    }

    // private final JwtFilter jwtFilter;
    // private final MyReactiveUserDetailsService userDetailsService;

    // public SecurityConfig(JwtFilter jwtFilter,  userDetailsService) {
    //     this.jwtFilter = jwtFilter;
    //     this.userDetailsService = userDetailsService;
    // }

    // @Bean
    // public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    //     return http
    //             .csrf(ServerHttpSecurity.CsrfSpec::disable)
    //             .authorizeExchange(auth -> auth
    //                     .pathMatchers("/auth/login").permitAll()
    //                     .anyExchange().authenticated()
    //             )
    //             .authenticationManager(reactiveAuthenticationManager(userDetailsService))
    //             .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    //             .build();
    // }


    // @Bean
    // public ReactiveAuthenticationManager reactiveAuthenticationManager(MyReactiveUserDetailsService userDetailsService) {
    //     UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
    //             new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    //     authenticationManager.setPasswordEncoder(passwordEncoder()); // Aquí se inyecta el encoder
    //     return authenticationManager;
    // }



}
