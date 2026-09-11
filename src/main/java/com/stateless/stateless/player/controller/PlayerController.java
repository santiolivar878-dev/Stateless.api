package com.stateless.stateless.player.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stateless.stateless.player.service.SpotifyApiService;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final SpotifyApiService spotifyApiService;

    public PlayerController(SpotifyApiService spotifyApiService) {
        this.spotifyApiService = spotifyApiService;
    }

    @PutMapping("/play")
    public ResponseEntity<Void> play(@RequestParam(required = false) String deviceId, Authentication authentication) {
        spotifyApiService.play(spotifyApiService.getValidAccessToken(authentication), deviceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/pause")
    public ResponseEntity<Void> pause(Authentication authentication) {
        spotifyApiService.pause(spotifyApiService.getValidAccessToken(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> next(Authentication authentication) {
        spotifyApiService.next(spotifyApiService.getValidAccessToken(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/previous")
    public ResponseEntity<Void> previous(Authentication authentication) {
        spotifyApiService.previous(spotifyApiService.getValidAccessToken(authentication));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestParam String deviceId, Authentication authentication) {
        spotifyApiService.transferPlayback(spotifyApiService.getValidAccessToken(authentication), deviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    public ResponseEntity<String> current(Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getCurrentlyPlaying(spotifyApiService.getValidAccessToken(authentication)));
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile(Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getCurrentUserProfile(spotifyApiService.getValidAccessToken(authentication)));
    }

    @PutMapping("/repeat")
    public ResponseEntity<Void> repeat(@RequestParam String state,
            @RequestParam(required = false) String deviceId, Authentication authentication) {
        spotifyApiService.setRepeatMode(spotifyApiService.getValidAccessToken(authentication), state, deviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/playlists")
    public ResponseEntity<String> playlists(@RequestParam(defaultValue = "0") int offset,
            Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getUserPlaylists(spotifyApiService.getValidAccessToken(authentication), offset));
    }

    @PutMapping("/play-context")
    public ResponseEntity<Void> playContext(@RequestParam String uri,
            @RequestParam(required = false) String deviceId, Authentication authentication) {
        spotifyApiService.playContext(spotifyApiService.getValidAccessToken(authentication), uri, deviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/playlist-tracks")
    public ResponseEntity<String> playlistTracks(@RequestParam String playlistId,
            @RequestParam(defaultValue = "0") int offset, Authentication authentication) {
        return ResponseEntity.ok(
                spotifyApiService.getPlaylistTracks(spotifyApiService.getValidAccessToken(authentication), playlistId,
                        offset));
    }

    @GetMapping("/album-tracks")
    public ResponseEntity<String> albumTracks(@RequestParam String albumId,
            @RequestParam(defaultValue = "0") int offset, Authentication authentication) {
        return ResponseEntity.ok(
                spotifyApiService.getAlbumTracks(spotifyApiService.getValidAccessToken(authentication), albumId,
                        offset));
    }

    @PutMapping("/play-track")
    public ResponseEntity<Void> playTrack(@RequestParam String contextUri, @RequestParam String trackUri,
            Authentication authentication) {
        spotifyApiService.playTrackInContext(spotifyApiService.getValidAccessToken(authentication), contextUri,
                trackUri);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recently-played")
    public ResponseEntity<String> recentlyPlayed(Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getRecentlyPlayed(spotifyApiService.getValidAccessToken(authentication)));
    }

    @GetMapping("/albums")
    public ResponseEntity<String> albums(Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getSavedAlbums(spotifyApiService.getValidAccessToken(authentication)));
    }

    @PutMapping("/play-single")
    public ResponseEntity<Void> playSingle(@RequestParam String trackUri, Authentication authentication) {
        spotifyApiService.playSingleTrack(spotifyApiService.getValidAccessToken(authentication), trackUri);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String q, Authentication authentication) {
        return ResponseEntity.ok(spotifyApiService.search(spotifyApiService.getValidAccessToken(authentication), q));
    }

    @GetMapping("/liked-songs")
    public ResponseEntity<String> likedSongs(Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.getLikedSongs(spotifyApiService.getValidAccessToken(authentication)));
    }

    @GetMapping("/is-liked")
    public ResponseEntity<Boolean> isLiked(@RequestParam String trackId, Authentication authentication) {
        return ResponseEntity
                .ok(spotifyApiService.isTrackLiked(spotifyApiService.getValidAccessToken(authentication), trackId));
    }

    @PutMapping("/like")
    public ResponseEntity<Void> like(@RequestParam String trackId, Authentication authentication) {
        spotifyApiService.likeTrack(spotifyApiService.getValidAccessToken(authentication), trackId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unlike")
    public ResponseEntity<Void> unlike(@RequestParam String trackId, Authentication authentication) {
        spotifyApiService.unlikeTrack(spotifyApiService.getValidAccessToken(authentication), trackId);
        return ResponseEntity.noContent().build();
    }

}