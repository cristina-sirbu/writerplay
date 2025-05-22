package com.cristina.writerplay.controller;

import com.cristina.writerplay.service.BigQueryReaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SummaryController {
    private final BigQueryReaderService readerService;

    public SummaryController(BigQueryReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/catalog-summary")
    public List<Map<String, Object>> catalogSummary() {
        /* FROM writerplay.enriched_songs, UNNEST(writers) AS writer
            => This explodes the writers array so that each row gets duplicated for each writer
            (e.g. a song with 2 writers will become 2 rows)

            GROUP BY writer
            => Groups all song entries by each individual writer

            COUNT(*) AS song_count
            => Counts how many songs that writer contributed to

            AVG(popularity) AS avg_popularity
            => Averages the popularity scores of the songs that writer was part of

            ORDER BY avg_popularity DESC
            => Sorts so the most “popular” writers (on average) are shown first

            LIMIT 10
            => Returns only the top 10 results
         */
        String sql = """
            SELECT writer, COUNT(*) AS song_count, AVG(popularity) AS avg_popularity
            FROM writerplay.enriched_songs, UNNEST(writers) AS writer
            GROUP BY writer
            ORDER BY avg_popularity DESC
            LIMIT 10
        """;

        return readerService.runQuery(sql);
    }

    @GetMapping("/songwriter-summary")
    public List<Map<String, Object>> songwriterSummary(@RequestParam String name) {
        /*
        WHERE 'Ed Sheeran' IN UNNEST(writers)
        => Filters for songs where the given name appears in the writers array

        ARRAY_TO_STRING(artists, ", ") AS artists
        => Turns the array of artists into a readable comma-separated string

        ORDER BY popularity DESC
        => Shows their most popular songs first

        LIMIT 10
        => Just shows the top 10 results
         */
        String sql = String.format("""
            SELECT title, isrc, popularity, ARRAY_TO_STRING(artists, ", ") AS artists
            FROM writerplay.enriched_songs
            WHERE '%s' IN UNNEST(writers)
            ORDER BY popularity DESC
            LIMIT 10
        """, name.replace("'", "\\'"));

        return readerService.runQuery(sql);
    }
}
