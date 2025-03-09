package com.ms_seguridad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.ms_seguridad.config.filter.JwtFilter;
import com.ms_seguridad.service.MyReactiveUserDetailsService;

@Configuration
public class SecurityConfig {

    // private final JwtFilter jwtFilter;

    // public SecurityConfig(JwtFilter jwtFilter) {
    //     this.jwtFilter = jwtFilter;
    // }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authManager) {
        return http
                .csrf(csrf -> csrf.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) 
                .authorizeExchange(auth -> {
                    auth.pathMatchers("/auth/**").permitAll();
                    auth.pathMatchers("/prueba/data").hasRole("ROLE_ADMIN"); //Authority("ADMIN"); // Solo ADMIN puede acceder
                    auth.anyExchange().authenticated(); // Requiere autenticación para cualquier otra ruta
                })
                .authenticationManager(authManager)
               // .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION) // Agregar el filtro JWT
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(MyReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder()); // Aquí se inyecta el encoder
        return authenticationManager;
    }

    // private final JwtFilter jwtFilter;
    // private final MyReactiveUserDetailsService userDetailsService;

    // public SecurityConfig(JwtFilter jwtFilter, MyReactiveUserDetailsService userDetailsService) {
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


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
