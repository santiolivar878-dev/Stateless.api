package com.stateless.stateless.player.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stateless.stateless.player.service.SpotifyApiService;

@RestController
public class TokenController {

    private final SpotifyApiService spotifyApiService;

    public TokenController(SpotifyApiService spotifyApiService) {
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/api/token")
    public Map<String, String> getToken(Authentication authentication) {
        String token = spotifyApiService.getValidAccessToken(authentication);
        return Map.of("token", token);
    }
}