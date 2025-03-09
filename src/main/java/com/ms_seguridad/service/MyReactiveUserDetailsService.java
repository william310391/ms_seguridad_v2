package com.ms_seguridad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ms_seguridad.entity.RolesEntity;
import com.ms_seguridad.model.request.AuthLoginRequest;
import com.ms_seguridad.model.response.AuthResponse;
import com.ms_seguridad.repository.PermissionsRepository;
import com.ms_seguridad.repository.RolesRepository;
import com.ms_seguridad.repository.UsersRepository;
import com.ms_seguridad.util.JwtUtil;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private PermissionsRepository permissionsRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usersRepository.findUserByUserName(username)
                .flatMap(user -> rolesRepository.findRoleByIdUser(user.id()) // Devuelve Flux<RolesEntity>
                        .collectList() // Convertimos Flux a Mono<List<RolesEntity>>
                        .flatMap(roleList -> {
                            // Extraemos los IDs de los roles
                            List<Integer> roleIds = roleList.stream()
                                    .map(RolesEntity::id) // Extraemos solo los IDs de los roles
                                    .collect(Collectors.toList());

                            if (roleIds.isEmpty()) {
                                return Mono.empty();
                            }

                            return permissionsRepository.findPermissionByIdRole(roleIds) // Pasamos List<Integer>
                                    .collectList() // Convertimos Flux a Mono<List<PermissionEntity>>
                                    .map(permissionList -> { // Convertimos permisos en UserDetails

                                        List<SimpleGrantedAuthority> authoryList = new ArrayList<>();

                                        roleList.stream().forEach(role -> {
                                            authoryList
                                                    .add(new SimpleGrantedAuthority("ROLE_".concat(role.role_name())));
                                        });

                                        permissionList.stream().forEach(permission -> {
                                            authoryList.add(new SimpleGrantedAuthority(permission.name()));
                                        });

                                        return User.withUsername(user.username())
                                                .password(user.password()) 
                                                .disabled(!Boolean.TRUE.equals(user.isEnabled())) 
                                                .accountExpired(!Boolean.TRUE.equals(user.isAccountNoExpired())) 
                                                .credentialsExpired(!Boolean.TRUE.equals(user.isCredentialNoExpired())) 
                                                .accountLocked(!Boolean.TRUE.equals(user.isAccountNoLocked())) 
                                                .authorities(authoryList)
                                                .build();
                                    });
                        }))
                .switchIfEmpty(Mono.empty()); // Retorna vacío si el usuario no existe
    }

    public Mono<AuthResponse> login(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        return authentication(username, password) // Retorna Mono<Authentication>
                .map(authentication -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String accessToken = jwtUtil.generateToken(authentication);
                    return new AuthResponse(username, "User logged successfully", accessToken, true);
                });
    }

    public Mono<Authentication> authentication(String username, String password) {
        return this.findByUsername(username)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password"))) // Si el usuario
                                                                                                        // no existe,
                                                                                                        // lanza error
                .flatMap(userDetails -> {
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Invalid password"));
                    }
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.getAuthorities());

                    return Mono.just(auth);
                });
    }

    public Mono<String> extractUsername(DecodedJWT decodedJWT) {
        return jwtUtil.extractUsername(decodedJWT);
    }

    public DecodedJWT validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
