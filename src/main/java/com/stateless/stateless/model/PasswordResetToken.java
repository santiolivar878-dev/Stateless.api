package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    private String email; 

    private String token;

    @Column(name = "created_at")
    private LocalDateTime expiryDate;

    // Constructor vacío obligatorio para JPA
    public PasswordResetToken() {}

    // Constructor que usa el PasswordResetService
    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }

    // Getters y Setters Manuales
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Método de validación que usa el Service
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}