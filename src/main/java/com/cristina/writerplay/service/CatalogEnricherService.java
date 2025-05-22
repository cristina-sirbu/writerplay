package com.cristina.writerplay.service;

import com.cristina.writerplay.model.Artist;
import com.cristina.writerplay.model.Song;
import com.cristina.writerplay.model.SpotifyTrack;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogEnricherService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifySearchService spotifySearchService;

    public CatalogEnricherService(SpotifyAuthService spotifyAuthService, SpotifySearchService spotifySearchService) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifySearchService = spotifySearchService;
    }

    public List<Song> enrichCatalog(List<Song> catalog) {
        for (Song song : catalog) {
            String token = spotifyAuthService.getAccessToken().block();
            if (token != null) {
                SpotifyTrack track = spotifySearchService.searchTrackByIsrc(song.getIsrc(), token).block();

                if (track != null) {
                    song.setSpotifyTrackId(track.id());
                    song.setPopularity(track.popularity());

                    List<String> artistNames = new ArrayList<>();
                    for (Artist artist : track.artists()) {
                        artistNames.add(artist.name());
                    }
                    song.setArtists(artistNames);
                }
            }
        }
        return catalog;
    }
}
