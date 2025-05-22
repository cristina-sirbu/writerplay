package com.cristina.writerplay.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
public class SpotifyAuthService {
    private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");

    public Mono<String> getAccessToken() {
        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException("SPOTIFY_CLIENT_ID or SPOTIFY_CLIENT_SECRET is not set");
        }

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        WebClient client = WebClient.builder()
                .baseUrl("https://accounts.spotify.com")
                .defaultHeader("Authorization", "Basic " + encodedCredentials)
                .build();

        return client.post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

    private record TokenResponse(String access_token, String token_type, int expires_in) {
        public String getAccessToken() {
            return access_token;
        }
    }
}
