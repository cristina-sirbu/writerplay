package com.cristina.writerplay.service;

import com.cristina.writerplay.exception.FileNotFoundInBucketException;
import com.cristina.writerplay.exception.InvalidCatalogFormatException;
import com.cristina.writerplay.model.Song;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CatalogReaderService {

    @Getter
    private final List<Song> catalog = new ArrayList<>();
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public List<Song> readCatalog() {
        String bucketName = "writerplay-catalogs";
        String fileName = "test_catalog.csv";

        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new FileNotFoundInBucketException("File not found in bucket: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Channels.newInputStream(blob.reader())))) {
            // Ignore header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                Song s = parseLine(line);
                catalog.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return catalog;
    }

    public static Song parseLine(String line) {
        String[] parts = line.split(",", -1); // -1 keeps empty fields

        if (parts.length < 3) {
            throw new InvalidCatalogFormatException("Malformed line: " + line);
        }

        String title = parts[0].trim();
        String isrc = parts[1].trim();
        List<String> writers = Arrays.stream(parts[2].split(";"))
                .map(String::trim)
                .toList();

        return new Song(title, isrc, writers);
    }

}
