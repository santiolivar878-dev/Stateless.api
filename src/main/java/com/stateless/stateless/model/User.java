package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "email_verified_at")
    private LocalDateTime email_verified_at;

    @Column(name = "email_verified")
    private boolean email_verified = false;

    @Column(name = "email_token")
    private String email_token;

    @Column(name = "email_token_expires_at")
    private LocalDateTime email_token_expires_at;

    @Column(name = "reset_token")
    private String reset_token;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime reset_token_expires_at;

    @Column(nullable = false)
    private String password;

    @Column(name = "remember_token")
    private String remember_token;

    @Column(nullable = false)
    private String estado = "activo"; // 'activo', 'inactivo'

    @ManyToOne(fetch = Connection.Lookup.PRIMARY) // Laravel role_id
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    // Métodos de UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "activo".equals(estado);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // La verificación por email se maneja en la lógica de negocio
    }
}