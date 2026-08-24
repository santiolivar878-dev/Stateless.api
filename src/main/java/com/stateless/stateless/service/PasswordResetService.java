package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public String createToken(String email) {
        return userRepository.findByEmail(email).map(user -> {
            // Eliminar tokens previos si existen
            tokenRepository.deleteByUser(user);
            
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user);
            tokenRepository.save(resetToken);
            return token;
        }).orElse(null);
    }

    public boolean validateAndReset(String token, String newPassword) {
        return tokenRepository.findByToken(token)
            .filter(t -> !t.isExpired())
            .map(t -> {
                User user = t.getUser();
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                tokenRepository.delete(t);
                return true;
            }).orElse(false);
    }
}
