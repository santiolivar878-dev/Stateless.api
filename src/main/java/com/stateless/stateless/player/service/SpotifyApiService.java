package com.stateless.stateless.player.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Envoltorio sobre la Web API de Spotify (api.spotify.com/v1/me/player/*).
 * Antes de cada llamada obtiene un access_token vigente (renovado si hacia
 * falta).
 */
@Service
public class SpotifyApiService {

    private static final String BASE_URL = "https://api.spotify.com/v1";

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final RestClient restClient;

    public SpotifyApiService(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
        this.restClient = RestClient.create();
    }

    /** Devuelve un access_token vigente para el usuario autenticado. */
    public String getValidAccessToken(Authentication authentication) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);
        if (client == null) {
            throw new IllegalStateException("No se pudo autorizar con Spotify. Vuelve a iniciar sesion.");
        }
        OAuth2AccessToken token = client.getAccessToken();
        return token.getTokenValue();
    }

    public void play(String accessToken, String deviceId) {
        String url = BASE_URL + "/me/player/play" + (deviceId != null ? "?device_id=" + deviceId : "");
        restClient.put()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public void pause(String accessToken) {
        restClient.put()
                .uri(BASE_URL + "/me/player/pause")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public void next(String accessToken) {
        restClient.post()
                .uri(BASE_URL + "/me/player/next")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public void previous(String accessToken) {
        restClient.post()
                .uri(BASE_URL + "/me/player/previous")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Transfiere la reproduccion al dispositivo del Web Playback SDK (el
     * navegador).
     */
    public void transferPlayback(String accessToken, String deviceId) {
        restClient.put()
                .uri(BASE_URL + "/me/player")
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("device_ids", new String[] { deviceId }, "play", true))
                .retrieve()
                .toBodilessEntity();
    }

    public String getCurrentlyPlaying(String accessToken) {
        return restClient.get()
                .uri(BASE_URL + "/me/player/currently-playing")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    public String getCurrentUserProfile(String accessToken) {
        return restClient.get()
                .uri(BASE_URL + "/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    public void setRepeatMode(String accessToken, String state, String deviceId) {
        String url = BASE_URL + "/me/player/repeat?state=" + state
                + (deviceId != null && !deviceId.isBlank() ? "&device_id=" + deviceId : "");
        restClient.put()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public String getUserPlaylists(String accessToken, int offset) {
        return restClient.get()
                .uri(BASE_URL + "/me/playlists?limit=50&offset=" + offset)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    /**
     * Reproduce una playlist/album completo en el dispositivo activo (context_uri).
     */
    public void playContext(String accessToken, String contextUri, String deviceId) {
        String url = BASE_URL + "/me/player/play"
                + (deviceId != null && !deviceId.isBlank() ? "?device_id=" + deviceId : "");
        restClient.put()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("context_uri", contextUri))
                .retrieve()
                .toBodilessEntity();
    }

    public String getPlaylistTracks(String accessToken, String playlistId, int offset) {
        try {
            return restClient.get()
                    .uri(BASE_URL + "/playlists/" + playlistId + "/items?limit=50&offset=" + offset)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException exception) {
            return restClient.get()
                    .uri(BASE_URL + "/playlists/" + playlistId + "/tracks?limit=50&offset=" + offset)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(String.class);
        }
    }

    public String getAlbumTracks(String accessToken, String albumId, int offset) {
        return restClient.get()
                .uri(BASE_URL + "/albums/" + albumId + "/tracks?limit=50&offset=" + offset)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    /**
     * Reproduce una cancion especifica dentro del contexto de su playlist/album.
     */
    public void playTrackInContext(String accessToken, String contextUri, String trackUri) {
        restClient.put()
                .uri(BASE_URL + "/me/player/play")
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of(
                        "context_uri", contextUri,
                        "offset", Map.of("uri", trackUri)))
                .retrieve()
                .toBodilessEntity();
    }

    public String getRecentlyPlayed(String accessToken) {
        return restClient.get()
                .uri(BASE_URL + "/me/player/recently-played?limit=20")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    public String getSavedAlbums(String accessToken) {
        return restClient.get()
                .uri(BASE_URL + "/me/albums?limit=20")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    /**
     * Reproduce una cancion suelta, sin contexto de playlist/album (usado en
     * "recientes").
     */
    public void playSingleTrack(String accessToken, String trackUri) {
        restClient.put()
                .uri(BASE_URL + "/me/player/play")
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("uris", new String[] { trackUri }))
                .retrieve()
                .toBodilessEntity();
    }

    public String search(String accessToken, String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return restClient.get()
                .uri(BASE_URL + "/search?q=" + encoded + "&type=track,album,artist,playlist&limit=10")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    public String getLikedSongs(String accessToken) {
        return restClient.get()
                .uri(BASE_URL + "/me/tracks?limit=50")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(String.class);
    }

    public boolean isTrackLiked(String accessToken, String trackId) {
        Boolean[] result = restClient.get()
                .uri(BASE_URL + "/me/tracks/contains?ids=" + trackId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Boolean[].class);
        return result != null && result.length > 0 && result[0];
    }

    public void likeTrack(String accessToken, String trackId) {
        restClient.put()
                .uri(BASE_URL + "/me/tracks?ids=" + trackId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public void unlikeTrack(String accessToken, String trackId) {
        restClient.delete()
                .uri(BASE_URL + "/me/tracks?ids=" + trackId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }
}