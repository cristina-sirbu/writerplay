package com.cristina.writerplay.service;

import com.cristina.writerplay.model.SearchResponse;
import com.cristina.writerplay.model.SpotifyTrack;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SpotifySearchService {

    private WebClient.Builder webClientBuilder;

    public SpotifySearchService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<SpotifyTrack> searchTrackByIsrc(String isrc, String token) {
        WebClient client = webClientBuilder
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", "isrc:" + isrc)
                        .queryParam("type", "track")
                        .build())
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .map(SearchResponse::firstTrack); // Custom logic to extract the first match
    }

}
