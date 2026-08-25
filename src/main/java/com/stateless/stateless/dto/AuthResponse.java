package com.stateless.stateless.dto;

public record AuthResponse(
    String token,
    UserResponse user
) {}