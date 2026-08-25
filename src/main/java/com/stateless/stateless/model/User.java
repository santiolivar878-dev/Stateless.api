package com.stateless.stateless.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
public class User implements UserDetails, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String estado = "activo";
    @Column(name = "email_verified")
    private boolean emailVerified = false;
    @Column(name = "email_token")
    private String emailToken;
    @Column(name = "email_token_expires_at")
    private LocalDateTime emailTokenExpiresAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public User() {}

    // GETTERS Y SETTERS OBLIGATORIOS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean v) { this.emailVerified = v; }
    public String getEmailToken() { return emailToken; }
    public void setEmailToken(String v) { this.emailToken = v; }
    public LocalDateTime getEmailTokenExpiresAt() { return emailTokenExpiresAt; }
    public void setEmailTokenExpiresAt(LocalDateTime v) { this.emailTokenExpiresAt = v; }

    // SEGURIDAD
    @Override public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @Override public String getUsername() { return email; }
    @Override public Map<String, Object> getAttributes() { return Map.of("email", email); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return "activo".equals(estado); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}