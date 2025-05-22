package com.cristina.writerplay.model;

public record SearchResponse(Tracks tracks) {
    public SpotifyTrack firstTrack() {
        return tracks.items().isEmpty() ? null: tracks.items().get(0);
    }
}