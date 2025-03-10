package com.ms_seguridad.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ms_seguridad.entity.UserRolesEntity;
import com.ms_seguridad.entity.UsersEntity;
import com.ms_seguridad.model.request.AuthCreateUserRequest;
import com.ms_seguridad.model.request.AuthLoginRequest;
import com.ms_seguridad.model.response.AuthResponse;
import com.ms_seguridad.repository.RolesRepository;
import com.ms_seguridad.repository.UserRolesRepository;
import com.ms_seguridad.repository.UsersRepository;
import com.ms_seguridad.service.AuthService;
import com.ms_seguridad.util.JwtUtil;

import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReactiveUserDetailsServiceImpl reactiveUserDetailsServiceImpl;
    @Override
    public Mono<AuthResponse> login(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();
    
        return reactiveUserDetailsServiceImpl.authentication(username, password) // Retorna Mono<Authentication>
            .flatMap(authentication -> { // Cambiar de .map() a .flatMap()
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String accessToken = jwtUtil.generateToken(authentication);
    
                return Mono.just( // Devolvemos directamente el Mono<AuthResponse>
                    AuthResponse.builder()
                        .username(username)
                        .message("User logged successfully")
                        .jwt(accessToken)
                        .status(true)
                        .build()
                );
            });
    }
    @Override
    public Mono<AuthResponse> register(AuthCreateUserRequest authCreateUserRequest) {
        String username = authCreateUserRequest.userName();
        String password = authCreateUserRequest.password();
        List<String> rolesRequest = authCreateUserRequest.roleRequest().roleListName();
    
        return userRepository.findUserByUserName(username)
            .flatMap(existingUser -> {
                System.out.println("El usuario " + username + " ya existe");
                return Mono.error(new RuntimeException("El usuario ya existe"));
            })
            .switchIfEmpty(Mono.defer(() -> 
                rolesRepository.findRoleByRoleName(rolesRequest)
                    .collectList()
                    .flatMap(roleList -> {
                        if (roleList.isEmpty()) {
                            System.out.println("El usuario " + username + " no tiene roles asignados");
                            return Mono.error(new RuntimeException("No se encontraron roles válidos"));
                        }
    
                        UsersEntity user = UsersEntity.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .isEnabled(true)
                            .isAccountNoLocked(true) 
                            .isAccountNoExpired(true)
                            .isCredentialNoExpired(true)
                            .build();
    
                        return userRepository.save(user)
                            .flatMap(savedUser -> {
                                List<UserRolesEntity> userRoles = roleList.stream()
                                    .map(role -> UserRolesEntity.builder()
                                        .idUser(savedUser.getId())
                                        .idRole(role.getId())
                                        .build()
                                    ).toList();
    
                                return userRolesRepository.saveAll(userRoles)
                                    .collectList()
                                    .thenReturn(AuthResponse.builder() // ⚠ Aquí se usa `thenReturn()` para forzar el tipo correcto
                                        .username(username)
                                        .message("Usuario creado")
                                        .build());
                            });
                    })
            ))
            .cast(AuthResponse.class) // ⚠ Esto fuerza la conversión explícita para evitar errores de tipo
            .doOnError(error -> System.err.println("Error al crear usuario: " + error.getMessage()));
    }



    // public Mono<AuthResponse> register(AuthCreateUserRequest authCreateUserRequest) {
    //     String username = authCreateUserRequest.userName();
    //     String password = authCreateUserRequest.password();
    //     List<String> rolesRequest = authCreateUserRequest.roleRequest().roleListName();
    
    //     userRepository.findUserByUserName(username)
    //             .flatMap(user -> {
    //                 System.out.println("El usuario " + username + " ya existe");
    //                 return Mono.error(new RuntimeException("El usuario ya existe"));
    //             });

    //     return rolesRepository.findRoleByRoleName(rolesRequest)
    //         .collectList()
    //         .flatMap(roleList -> {
    //             if (roleList.isEmpty()) {
    //                 System.out.println("El usuario " + username + " no tiene roles asignados");
    //                 return Mono.error(new RuntimeException("No se encontraron roles válidos"));
    //             }
    
    //             UsersEntity user = UsersEntity.builder()
    //                 .username(username)
    //                 .password(passwordEncoder.encode(password))
    //                 .isEnabled(true)
    //                 .isAccountNoLocked(true) 
    //                 .isAccountNoExpired(true)
    //                 .isCredentialNoExpired(true)
    //                 .build();
    
    //             return userRepository.save(user)
    //                 .flatMap(savedUser -> {
    //                     List<UserRolesEntity> userRoles = roleList.stream()
    //                         .map(role -> UserRolesEntity.builder()
    //                             .idUser(savedUser.getId())
    //                             .idRole(role.getId())
    //                             .build()
    //                         ).toList(); // Se corrigió la sintaxis aquí
    
    //                     return userRolesRepository.saveAll(userRoles)
    //                         .collectList()
    //                         .map(savedRoles -> {
    //                             System.out.println("Usuario creado: " + savedUser);
    //                             return AuthResponse.builder()
    //                                 .username(username)
    //                                 .message("Usuario creado")
    //                                 .build(); // Se eliminó el `Mono.just(...)` innecesario
    //                         });
    //                 });
    //         })
    //         .doOnError(error -> System.err.println("Error al crear usuario: " + error.getMessage()));
    // }

}
