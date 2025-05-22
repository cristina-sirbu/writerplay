package com.cristina.writerplay.service;

import com.cristina.writerplay.model.Song;
import com.google.cloud.bigquery.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BigQueryWriterService {

    private static final Logger logger = LoggerFactory.getLogger(BigQueryWriterService.class);

    private final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
    private final String datasetName = "writerplay";
    private final String tableName = "enriched_songs";

    public void writeSongs(List<Song> songs) {
        TableId tableId = TableId.of(datasetName, tableName);

        List<InsertAllRequest.RowToInsert> rows = new ArrayList<>();

        for (Song song : songs) {
            Map<String, Object> row = new HashMap<>();
            row.put("title", song.getTitle());
            row.put("isrc", song.getIsrc());
            row.put("writers", song.getWriters());
            row.put("spotifyTrackId", song.getSpotifyTrackId());
            row.put("popularity", song.getPopularity());
            row.put("artists", song.getArtists());

            rows.add(InsertAllRequest.RowToInsert.of(row));
        }

        InsertAllRequest request = InsertAllRequest.newBuilder(tableId).setRows(rows).build();
        InsertAllResponse response = bigQuery.insertAll(request);

        if (response.hasErrors()) {
            for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                logger.error("Error inserting row {}: {}", entry.getKey(), entry.getValue());
            }
        } else {
            logger.info("All rows inserted into BigQuery.");
        }
    }
}
