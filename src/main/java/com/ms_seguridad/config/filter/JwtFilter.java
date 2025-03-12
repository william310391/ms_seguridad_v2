package com.ms_seguridad.config.filter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.http.HttpStatus;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_seguridad.service.Impl.ReactiveUserDetailsServiceImpl;


import reactor.core.publisher.Mono;

public class JwtFilter implements WebFilter {

    @Autowired
    private ReactiveUserDetailsServiceImpl userDetailsService;
    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
    

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange,@NonNull WebFilterChain chain) {

        // System.err.println(exchange.getRequest().getPath());
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);    
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
    
            try {
                DecodedJWT decodedJWT = userDetailsService.validateToken(token);
    
                return userDetailsService.extractUsername(decodedJWT)
                    .flatMap(username -> userDetailsService.findByUsername(username)) // Omitir si `validateToken()` ya retorna el usuario
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(),
                                userDetails.getPassword(),
                                userDetails.getAuthorities()
                        );
    
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
    
                        return securityContextRepository.save(exchange, context)
                                .then(chain.filter(exchange))
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                    })
                    .onErrorResume(error -> sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Access Denied: " + error.getMessage())); // Se quitó `return`
            } catch (Exception e) {
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token: " + e.getMessage());
            }
        }
    
        return chain.filter(exchange);
        //return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,"unrecorded token");
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            // Crear un objeto JSON con Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            String errorJson = objectMapper.writeValueAsString(Map.of("error", message));

            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(errorJson.getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
