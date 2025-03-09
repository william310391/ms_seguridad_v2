package com.ms_seguridad.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ms_seguridad.service.MyReactiveUserDetailsService;


import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

    // @Autowired
    // private JwtUtil jwtUtil;
    // @Autowired
    // private MyReactiveUserDetailsService userDetailsService;
    // private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
    
   
    private final MyReactiveUserDetailsService userDetailsService;
    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();

    public JwtFilter(MyReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            DecodedJWT decodedJWT= userDetailsService.validateToken(token);
            return userDetailsService.extractUsername(decodedJWT)
                    .flatMap(username -> userDetailsService.findByUsername(username)
                            .flatMap(userDetails -> {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                                        userDetails.getPassword(),
                                        userDetails.getAuthorities());

                                SecurityContext context = SecurityContextHolder.createEmptyContext();
                                context.setAuthentication(authentication);
                                SecurityContextHolder.setContext(context);
                                return securityContextRepository.save(exchange, context).then(chain.filter(exchange));
                            }))
                    .switchIfEmpty(chain.filter(exchange));
        }

        return chain.filter(exchange);
    }
}
