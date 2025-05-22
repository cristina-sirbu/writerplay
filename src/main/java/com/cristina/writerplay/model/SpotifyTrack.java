package com.cristina.writerplay.model;

import java.util.List;

public record SpotifyTrack(String id, String name, int popularity, List<Artist> artists) {}