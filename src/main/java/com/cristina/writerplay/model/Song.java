package com.cristina.writerplay.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Song {

    private String title;
    private String isrc;
    private List<String> writers;

    // Spotify metadata
    private String spotifyTrackId;
    private List<String> artists;
    private Integer popularity;

    public Song(String title, String isrc, List<String> writers) {
        this.title = title;
        this.isrc = isrc;
        this.writers = writers;
    }

}
