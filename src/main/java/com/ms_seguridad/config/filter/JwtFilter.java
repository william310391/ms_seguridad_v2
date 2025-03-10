package com.ms_seguridad.config.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.ms_seguridad.service.MyReactiveUserDetailsService;


import reactor.core.publisher.Mono;

public class JwtFilter implements WebFilter {

    @Autowired
    private MyReactiveUserDetailsService userDetailsService;
    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
    

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
    
            try {
                DecodedJWT decodedJWT = userDetailsService.validateToken(token);
    
                return userDetailsService.extractUsername(decodedJWT)
                        .flatMap(userDetailsService::findByUsername)
                        .flatMap(userDetails -> {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails.getUsername(),
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities());
    
                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
    
                            return securityContextRepository.save(exchange, context)
                                    .then(chain.filter(exchange))
                                    .contextWrite(
                                            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

                            // return chain.filter(exchange)
                            // .then(securityContextRepository.save(exchange, context))
                            // .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

                        })
                        .onErrorResume(error -> sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Access Denied: " + error.getMessage()));
            } catch (Exception e) {
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token: " + e.getMessage());
            }
        }
    
        return chain.filter(exchange);
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorJson = "{\"error\": \"" + message + "\"}";

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(errorJson.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
