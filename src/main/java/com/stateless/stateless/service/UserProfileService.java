package com.stateless.stateless.service;

import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public void updateBasicInfo(Long userId, String name, String email) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }

    @Transactional
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();

        // Verificar si la contraseña actual coincide (Equivalente a Hash::check)
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}