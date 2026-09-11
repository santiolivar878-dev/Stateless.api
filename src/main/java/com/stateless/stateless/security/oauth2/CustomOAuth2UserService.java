package com.stateless.stateless.security.oauth2;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.stateless.stateless.model.Role;
import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.RoleRepository;
import com.stateless.stateless.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private RoleRepository roleRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Cargar el usuario desde Google
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        if (name == null || name.isBlank()) {
            name = oauth2User.getAttribute("display_name");
        }
        final String displayName = name;

        // Buscar en la BD o crear uno nuevo (Equivalencia a GoogleController.php)
        return userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setName(displayName);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setEstado("activo");
            newUser.setEmailVerified(true);
            newUser.setEmailTokenExpiresAt(LocalDateTime.now());
            
            // Asignar rol cliente
            Role clienteRole = roleRepository.findByName("cliente").orElse(null);
            newUser.setRole(clienteRole);
            
            return userRepository.save(newUser);
        });
    }
}