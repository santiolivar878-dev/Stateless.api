package com.stateless.stateless.service;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.PasswordResetToken;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public String createToken(String email) {
        return userRepository.findByEmail(email).map(user -> {
            tokenRepository.deleteByEmail(email); // Borramos por email plano
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user);
            tokenRepository.save(resetToken);
            return token;
        }).orElse(null);
    }

    @Transactional
    public boolean validateAndReset(String token, String newPassword) {
        return tokenRepository.findByToken(token)
            .filter(t -> !t.isExpired())
            .map(t -> {
                User user = userRepository.findByEmail(t.getEmail()).orElse(null);
                if (user != null) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    tokenRepository.delete(t);
                    return true;
                }
                return false;
            }).orElse(false);
    }
}