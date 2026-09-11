package com.stateless.stateless.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.STORAGE;

import com.stateless.stateless.security.CustomLoginSuccessHandler;
import com.stateless.stateless.security.oauth2.CustomOAuth2UserService;


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
            // 1. Desactivar el almacenamiento en caché del navegador en rutas protegidas
            .headers(headers -> headers
                .cacheControl(cache -> {})
            )
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos y rutas públicas de la tienda y del reproductor
                .requestMatchers("/css/**", "/js/**", "/images/**", "/videos/**", "/favicon.ico", "/favicon.svg", "/error", "/error/**").permitAll()
                .requestMatchers("/player/skins/**", "/player/js/**", "/favicon.svg").permitAll()
                .requestMatchers("/", "/login", "/register", "/essentials", "/octane", "/waves", "/life", "/buscar/**", "/catalogo/**").permitAll()
                .requestMatchers("/producto/**", "/carrito", "/carrito/**", "/forgot-password", "/reset-password/**").permitAll()
                .requestMatchers("/player/login", "/oauth2/**").permitAll()

                // 2. Funcionalidad del reproductor de Spotify (disponible para cualquier usuario autenticado)
                .requestMatchers("/player", "/player/**", "/api/player/**", "/api/token").authenticated()
                
                // 3. RUTAS EXCLUSIVAS DE ADMIN (Usuarios y Reportes)
                .requestMatchers("/admin/usuarios/**", "/admin/reportes/**").hasRole("ADMIN")
                
                // 4. RUTAS COMPARTIDAS (Admin y Empleado gestionan la tienda)
                .requestMatchers("/admin/ventas/**", "/admin/envios/**", "/admin/productos/**", "/admin/categorias/**", "/admin/proveedores/**", "/admin/dashboard").hasAnyRole("ADMIN", "EMPLEADO")
                
                // 5. RUTAS DE CLIENTE
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
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)           // Invalida la sesión actual en el servidor
                .clearAuthentication(true)             // Limpia el contexto de seguridad
                .deleteCookies("JSESSIONID")           // Elimina la cookie de sesión del navegador
                .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES, STORAGE))) // Limpia almacenamiento y cookies
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