package com.stateless.stateless.dto;

public record UserResponse(
    Long id,
    String name,
    String email,
    String role
) {}