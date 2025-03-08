package com.ms_seguridad.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.core.Authentication;

// import com.ms_seguridad.model.request.AuthRequest;
// import com.ms_seguridad.util.JwtUtil;

// import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

//     @Autowired
//     private JwtUtil jwtUtil;
//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Autowired
//     private PasswordEncoder passwordEncoder;


//     // public AuthController(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
//     //     this.jwtUtil = jwtUtil;
//     //     this.userDetailsService = userDetailsService;
//     // }

//     @PostMapping("/login")
//     public Mono<String> login(@RequestBody AuthRequest request) {
//         return userDetailsService.loadUserByUsername(request.getUsername())
//                 .map(userDetails -> {
//                     if ("password".equals(request.getPassword())) { // Aquí deberías usar BCryptPasswordEncoder
//                         Authentication authentication = authentication(AuthRequest.username(), AuthRequest.password());
//                         return jwtUtil.createToken(authentication);
//                     }
//                     throw new RuntimeException("Credenciales incorrectas");
//                 });
//     }

//             public Authentication authentication(String username, String password) {
//                 UserDetails userDetails = this.loadUserByUsername(username);

//                 if (userDetails == null) {
//                         throw new BadCredentialsException("Invalid username or password");
//                 }

//                 if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//                         throw new BadCredentialsException("Invalid password");
//                 }

//                 return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
//         }
}


// https://chatgpt.com/c/67ca69c6-7dec-800d-abce-a97f042e0f9a