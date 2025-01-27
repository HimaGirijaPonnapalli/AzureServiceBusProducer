package com.example.demoSBus.Services;



import com.example.demoSBus.Models.DataModel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    private FileReader fileReader;

    @BeforeEach
    void setUp() {
        fileReader = new FileReader();
    }

    @Test
    void testReadJsonFile_validFile() throws IOException {
        // Arrange
        String jsonContent = """
                [
                    {"id": 1, "name": "John Doe", "email": "john@example.com"},
                    {"id": 2, "name": "Jane Doe", "email": "jane@example.com"}
                ]
                """;

        Path tempFile = Files.createTempFile("test", ".json");
        Files.writeString(tempFile, jsonContent);

        // Act
        List<DataModel> dataModels = fileReader.readJsonFile(tempFile.toString());

        // Assert
        assertNotNull(dataModels);
        assertEquals(2, dataModels.size());
        assertEquals(1, dataModels.get(0).getId());
        assertEquals("John Doe", dataModels.get(0).getName());
        assertEquals("john@example.com", dataModels.get(0).getEmail());

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReadJsonFile_invalidFilePath() {
        // Arrange
        String invalidFilePath = "nonexistent.json";

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            fileReader.readJsonFile(invalidFilePath);
        });

        assertTrue(exception.getMessage().contains("nonexistent.json"));
    }

    @Test
    void testReadJsonFile_invalidJsonFormat() throws IOException {
        // Arrange
        String invalidJson = "invalid json content";
        Path tempFile = Files.createTempFile("invalid", ".json");
        Files.writeString(tempFile, invalidJson);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileReader.readJsonFile(tempFile.toString());
        });

        // Cleanup
        Files.deleteIfExists(tempFile);
    }
}

