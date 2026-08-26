package com.stateless.stateless.config;

import com.stateless.stateless.security.CustomLoginSuccessHandler;
import com.stateless.stateless.security.oauth2.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomLoginSuccessHandler successHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomLoginSuccessHandler successHandler, 
                          CustomOAuth2UserService customOAuth2UserService) {
        this.successHandler = successHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos y rutas básicas de error
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                
                // 2. Vistas públicas de la tienda (ABIERTAS AL PÚBLICO)
                .requestMatchers("/", "/login", "/register", "/essentials", "/octane", "/waves", "/buscar/**", "/catalogo/**").permitAll()
                
                // 3. Detalle de producto y Carrito (ABIERTOS AL PÚBLICO)
                .requestMatchers("/producto/**", "/carrito", "/carrito/**").permitAll()
                
                // 4. Rutas protegidas (REQUIEREN LOGIN)
                .requestMatchers("/admin/**", "/reportes/**").hasRole("ADMIN")
                .requestMatchers("/empleado/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/account/**", "/checkout/**").authenticated() 
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(successHandler)
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}