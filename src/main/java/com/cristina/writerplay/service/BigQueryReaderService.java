package com.cristina.writerplay.service;

import com.google.cloud.bigquery.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BigQueryReaderService {

    private final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();

    public List<Map<String, Object>> runQuery(String sql) {
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql).build();

        try {
            TableResult result = bigQuery.query(config);
            List<Map<String, Object>> rows = new ArrayList<>();

            for (FieldValueList row : result.iterateAll()) {
                Map<String, Object> map = new HashMap<>();
                Schema schema = result.getSchema();

                for (Field field : schema.getFields()) {
                    map.put(field.getName(), row.get(field.getName()).getValue());
                }
                rows.add(map);
            }
            return rows;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Query interrupted", e);
        }
    }
}
