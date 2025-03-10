package com.ms_seguridad.service;

import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println("Buscando usuario: " + username);
    
        return usersRepository.findUserByUserName(username)
            .doOnNext(user -> System.out.println("Usuario encontrado: " + user))
            .switchIfEmpty(Mono.defer(() -> {
                System.out.println("Usuario no encontrado: " + username);
                return Mono.empty();
            }))
            .flatMap(user -> rolesRepository.findRoleByIdUser(user.id())
                .doOnNext(role -> System.out.println("Rol encontrado: " + role))
                .doOnError(error -> System.err.println("Error al obtener roles: " + error.getMessage()))
                .collectList()
                .flatMap(roleList -> {
                    if (roleList.isEmpty()) {
                        System.out.println("El usuario " + username + " no tiene roles asignados");
                        return Mono.empty();
                    }
    
                    List<Integer> roleIds = roleList.stream().map(RolesEntity::id).toList();
    
                    return permissionsRepository.findPermissionByIdRole(roleIds)
                        .doOnNext(permission -> System.out.println("Permiso encontrado: " + permission))
                        .doOnError(error -> System.err.println("Error al obtener permisos: " + error.getMessage()))
                        .collectList()
                        .map(permissionList -> {
                            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
    
                            roleList.forEach(role ->
                                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.role_name()))
                            );
    
                            permissionList.forEach(permission ->
                                authorityList.add(new SimpleGrantedAuthority(permission.name()))
                            );
    
                            System.out.println("Usuario " + username + " tiene " + roleList.size() + " roles y " + permissionList.size() + " permisos");
    
                            return User.withUsername(user.username())
                                    .password(user.password())
                                    .disabled(!Boolean.TRUE.equals(user.isEnabled()))
                                    .accountExpired(!Boolean.TRUE.equals(user.isAccountNoExpired()))
                                    .credentialsExpired(!Boolean.TRUE.equals(user.isCredentialNoExpired()))
                                    .accountLocked(!Boolean.TRUE.equals(user.isAccountNoLocked()))
                                    .authorities(authorityList)
                                    .build();
                        });
                })
            )
            .doOnError(error -> System.err.println("Error general en findByUsername: " + error.getMessage()))
            .switchIfEmpty(Mono.empty());
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
                .doOnError(error -> System.err.println("Error al buscar usuario: " + error.getMessage())) // Log error en findByUsername
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password"))) 
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
