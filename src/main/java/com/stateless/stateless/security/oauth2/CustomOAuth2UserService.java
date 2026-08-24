package com.stateless.stateless.security.oauth2;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Role;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Lógica de creación (equivalente a User::create en GoogleController)
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Password aleatorio
            newUser.setEstado("activo");
            newUser.setEmail_verified(true);
            newUser.setEmail_verified_at(LocalDateTime.now());
            
            Role clienteRole = roleRepository.findByName("cliente").orElseThrow();
            newUser.setRole(clienteRole);
            
            return userRepository.save(newUser);
        });

        // Verificación de estado inactivo (equivalente a tu validación en Laravel)
        if ("inactivo".equals(user.getEstado())) {
            throw new OAuth2AuthenticationException("Tu cuenta está deshabilitada.");
        }

        return user; // La entidad User ya implementa UserDetails/OAuth2User
    }
}