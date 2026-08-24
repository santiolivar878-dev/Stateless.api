package com.stateless.stateless.service;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Role;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registrarCliente(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        // Asignar rol cliente (ID 3 en tu seeder de Laravel)
        Role clientRole = roleRepository.findByName("cliente").orElse(null);
        user.setRole(clientRole);
        
        user.setEmail_verified(false);
        user.setEstado("activo");
        user.setEmail_token(UUID.randomUUID().toString());
        user.setEmail_token_expires_at(LocalDateTime.now().plusHours(24));
        
        return userRepository.save(user);
    }

    public boolean verificarEmail(String token) {
        return userRepository.findByEmailToken(token)
            .filter(user -> user.getEmail_token_expires_at().isAfter(LocalDateTime.now()))
            .map(user -> {
                user.setEmail_verified(true);
                user.setEmail_verified_at(LocalDateTime.now());
                user.setEmail_token(null);
                user.setEmail_token_expires_at(null);
                userRepository.save(user);
                return true;
            }).orElse(false);
    }
}