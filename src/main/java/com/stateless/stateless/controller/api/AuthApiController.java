package com.stateless.stateless.controller.api;

import com.stateless.stateless.dto.*;
import com.stateless.stateless.model.User;
import com.stateless.stateless.service.AuthService;
import com.stateless.stateless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.get("email"), request.get("password"))
            );
            
            User user = (User) authentication.getPrincipal();
            // Nota: En una app real aquí generarías un JWT. 
            // Para la equivalencia con Laravel, devolvemos un token simulado o el token de sesión.
            String token = "stateless-token-" + user.getId(); 

            return ResponseEntity.ok(new AuthResponse(token, new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole().getName()
            )));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "Credenciales incorrectas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        User user = authService.registrarCliente(
            request.get("name"), 
            request.get("email"), 
            request.get("password")
        );

        String token = "stateless-token-" + user.getId();
        return ResponseEntity.ok(new AuthResponse(token, new UserResponse(
            user.getId(), user.getName(), user.getEmail(), "cliente"
        )));
    }

    @GetMapping("/usuario")
    public ResponseEntity<?> usuario(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of("user", new UserResponse(
            user.getId(), user.getName(), user.getEmail(), user.getRole().getName()
        )));
    }
}