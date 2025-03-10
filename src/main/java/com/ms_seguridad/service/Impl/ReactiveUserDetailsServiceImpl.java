package com.ms_seguridad.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ms_seguridad.repository.PermissionsRepository;
import com.ms_seguridad.repository.RolesRepository;
import com.ms_seguridad.repository.UsersRepository;
import com.ms_seguridad.util.JwtUtil;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

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
            .flatMap(user -> rolesRepository.findRoleByIdUser(user.getId())
                .doOnNext(role -> System.out.println("Rol encontrado: " + role))
                .doOnError(error -> System.err.println("Error al obtener roles: " + error.getMessage()))
                .collectList()
                .flatMap(roleList -> {
                    if (roleList.isEmpty()) {
                        System.out.println("El usuario " + username + " no tiene roles asignados");
                        return Mono.empty();
                    }
    
                    List<Integer> roleIds = roleList.stream().map(role -> role.getId()).toList();
    
                    return permissionsRepository.findPermissionByIdRole(roleIds)
                        .doOnNext(permission -> System.out.println("Permiso encontrado: " + permission))
                        .doOnError(error -> System.err.println("Error al obtener permisos: " + error.getMessage()))
                        .collectList()
                        .map(permissionList -> {
                            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
    
                            roleList.forEach(role ->
                                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRole_name()))
                            );
    
                            permissionList.forEach(permission ->
                                authorityList.add(new SimpleGrantedAuthority(permission.getName()))
                            );
    
                            System.out.println("Usuario " + username + " tiene " + roleList.size() + " roles y " + permissionList.size() + " permisos");
    
                            return User.withUsername(user.getUsername())
                                    .password(user.getPassword())
                                    .disabled(!Boolean.TRUE.equals(user.getIsEnabled()))
                                    .accountExpired(!Boolean.TRUE.equals(user.getIsAccountNoExpired()))
                                    .credentialsExpired(!Boolean.TRUE.equals(user.getIsCredentialNoExpired()))
                                    .accountLocked(!Boolean.TRUE.equals(user.getIsAccountNoLocked()))
                                    .authorities(authorityList)
                                    .build();
                        });
                })
            )
            .doOnError(error -> System.err.println("Error general en findByUsername: " + error.getMessage()))
            .switchIfEmpty(Mono.empty());
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
