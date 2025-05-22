package com.cristina.writerplay.controller;

import com.cristina.writerplay.model.Song;
import com.cristina.writerplay.service.BigQueryWriterService;
import com.cristina.writerplay.service.CatalogEnricherService;
import com.cristina.writerplay.service.CatalogReaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    private final CatalogReaderService catalogReaderService;
    private final CatalogEnricherService catalogEnricherService;
    private final BigQueryWriterService bigQueryWriterService;

    public CatalogController(CatalogReaderService catalogReaderService, CatalogEnricherService catalogEnricherService, BigQueryWriterService bigQueryWriterService) {
        this.catalogReaderService = catalogReaderService;
        this.catalogEnricherService = catalogEnricherService;
        this.bigQueryWriterService = bigQueryWriterService;
    }

    @GetMapping("/read-catalog")
    public List<Song> readCatalog() {
        return catalogReaderService.readCatalog();
    }

    @GetMapping("/enriched-catalog")
    public List<Song> getEnrichedCatalog() {
        List<Song> catalog = catalogReaderService.readCatalog();
        return catalogEnricherService.enrichCatalog(catalog);
    }

    @GetMapping("/write-bq")
    public String writeToBigQuery() {
        List<Song> catalog = catalogReaderService.readCatalog();
        List<Song> enriched = catalogEnricherService.enrichCatalog(catalog);
        bigQueryWriterService.writeSongs(enriched);
        return "Written to BigQuery!";
    }
}
