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
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User registrarCliente(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role clientRole = roleRepository.findByName("cliente").orElse(null);
        user.setRole(clientRole);
        user.setEmailVerified(false);
        user.setEstado("activo");
        user.setEmailToken(UUID.randomUUID().toString());
        user.setEmailTokenExpiresAt(LocalDateTime.now().plusHours(24));
        return userRepository.save(user);
    }

    public boolean verificarEmail(String token) {
        return userRepository.findByEmailToken(token)
            .filter(user -> user.getEmailTokenExpiresAt().isAfter(LocalDateTime.now()))
            .map(user -> {
                user.setEmailVerified(true);
                user.setEmailToken(null);
                user.setEmailTokenExpiresAt(null);
                userRepository.save(user);
                return true;
            }).orElse(false);
    }
}