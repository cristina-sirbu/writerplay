package com.cristina.writerplay;

import com.cristina.writerplay.exception.InvalidCatalogFormatException;
import com.cristina.writerplay.model.Song;
import com.cristina.writerplay.service.CatalogReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CatalogReaderServiceTest {

    private CatalogReaderService service;

    @BeforeEach
    public void setup() {
        service = new CatalogReaderService();
    }

    @Test
    public void testParseValidCsvLine() {
        String line = "Blinding Lights,USUG11904269,Abel Tesfaye;Max Martin";
        Song song = service.parseLine(line);

        assertEquals("Blinding Lights", song.getTitle());
        assertEquals("USUG11904269", song.getIsrc());
        assertEquals(List.of("Abel Tesfaye", "Max Martin"), song.getWriters());
    }

    @Test
    public void testParseInvalidLineThrows() {
        String line = "Incomplete,USUG123";
        assertThrows(InvalidCatalogFormatException.class, () -> service.parseLine(line));
    }
}
