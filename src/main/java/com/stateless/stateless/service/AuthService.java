package com.stateless.stateless.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 Importante

import com.stateless.stateless.model.Role;
import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.RoleRepository;
import com.stateless.stateless.repository.UserRepository;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional // 👈 Asegura que el registro quede guardado en BD
    public User registrarCliente(String name, String email, String password) {
        // Validar si el correo ya existe en la base de datos
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con este correo electrónico.");
        }

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

    @Transactional // 👈 INDISPENSABLE: Sin esto, nunca guarda el email_verified = 1 en MySQL
    public boolean verificarEmail(String token) {
        return userRepository.findByEmailToken(token)
            .filter(user -> user.getEmailTokenExpiresAt() != null && user.getEmailTokenExpiresAt().isAfter(LocalDateTime.now()))
            .map(user -> {
                user.setEmailVerified(true);
                user.setEmailToken(null);
                user.setEmailTokenExpiresAt(null);
                userRepository.save(user);
                return true;
            }).orElse(false);
    }
}