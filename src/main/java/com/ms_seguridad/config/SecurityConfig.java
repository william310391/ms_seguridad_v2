package com.ms_seguridad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, 
                                                         ReactiveAuthenticationManager authManager, 
                                                         JwtFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Desactiva CSRF
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // No guarda contexto de seguridad
                .authenticationManager(authManager) // Usa el AuthenticationManager proporcionado
                .authorizeExchange(auth -> {

                    // 🔥 Permitir acceso libre a Actuator
                    auth.pathMatchers("/actuator/**").permitAll();

                    // 🔥 Permitir acceso libre a swagger
                    auth.pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml", "/webjars/**","/favicon.ico").permitAll();

                    // Permitir acceso público a login y register
                    auth.pathMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    // auth.pathMatchers(HttpMethod.POST, "/api/seguridad/auth/login").permitAll();

                    auth.pathMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN");
                    // auth.pathMatchers(HttpMethod.POST, "/api/seguridad/auth/register").hasRole("ADMIN");




                    // Todas las demás rutas requieren autenticación
                    auth.anyExchange().authenticated();
                })
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION) // Agregar filtro JWT
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // 🔥 Deshabilita autenticación básica
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsServiceImpl userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder()); // Aquí se inyecta el encoder
        return authenticationManager;
    }


}
