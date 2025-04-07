package com.pjsoft.j2arch.uml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationManagerTest {

    private ConfigurationManager configManager;
    private Path tempInputDir;
    private Path tempOutputDir;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize the ConfigurationManager singleton
        configManager = ConfigurationManager.getInstance();
        configManager.clearSettings();

        // Set up temporary directories for testing
        tempInputDir = Files.createTempDirectory("input");
        tempOutputDir = Files.createTempDirectory("output");
    }

    @Test
    void testLoadDefaultConfig() {
        // Act
        configManager.loadDefaultConfig();

        // Assert
        assertEquals("./input", configManager.getProperty("input.directory"), "Default input directory should be './input'");
        assertEquals("./output", configManager.getProperty("output.directory"), "Default output directory should be './output'");
        assertEquals("class,sequence", configManager.getProperty("diagram.types"), "Default diagram types should be 'class,sequence'");
        assertEquals("", configManager.getProperty("include.package"), "Default include.package should be empty");
    }

    @Test
    void testSetProperty_ValidInputDirectory() {
        // Act
        configManager.setProperty("input.directory", tempInputDir.toString());

        // Assert
        assertEquals(tempInputDir.toString(), configManager.getProperty("input.directory"), "Input directory should be set correctly");
    }

    @Test
    void testSetProperty_InvalidInputDirectory() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.setProperty("input.directory", "nonexistent_directory");
        });

        assertTrue(exception.getMessage().contains("input.directory must be a valid directory: nonexistent_directory"),
                "Exception message should indicate invalid input directory");
    }

    @Test
    void testSetProperty_ValidOutputDirectory() {
        // Act
        configManager.setProperty("output.directory", tempOutputDir.toString());

        // Assert
        assertEquals(tempOutputDir.toString(), configManager.getProperty("output.directory"), "Output directory should be set correctly");
    }



    @Test
    void testSetProperty_ValidDiagramTypes() {
        // Act
        configManager.setProperty("diagram.types", "class,sequence");

        // Assert
        assertEquals("class,sequence", configManager.getProperty("diagram.types"), "Diagram types should be set correctly");
    }

    @Test
    void testSetProperty_InvalidDiagramTypes() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.setProperty("diagram.types", "invalidType");
        });

        assertTrue(exception.getMessage().contains("Invalid diagram type: invalidType"),
                "Exception message should indicate invalid diagram type");
    }

    @Test
    void testLoadCustomConfig_ValidInputs() {
        // Arrange
        Map<String, String> customInputs = new HashMap<>();
        customInputs.put("input.directory", tempInputDir.toString());
        customInputs.put("output.directory", tempOutputDir.toString());
        customInputs.put("diagram.types", "class");

        // Act
        configManager.loadCustomConfig(customInputs);

        // Assert
        assertEquals(tempInputDir.toString(), configManager.getProperty("input.directory"), "Custom input directory should be set correctly");
        assertEquals(tempOutputDir.toString(), configManager.getProperty("output.directory"), "Custom output directory should be set correctly");
        assertEquals("class", configManager.getProperty("diagram.types"), "Custom diagram types should be set correctly");
    }

    @Test
    void testLoadCustomConfig_InvalidInputs() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.loadCustomConfig(null);
        });

        assertTrue(exception.getMessage().contains("Custom inputs cannot be null or empty"),
                "Exception message should indicate null or empty custom inputs");
    }

    @Test
    void testLoadFileConfig_ValidFile() throws IOException {
        // Arrange
        Path tempConfigFile = Files.createTempFile("config", ".properties");
        try (FileWriter writer = new FileWriter(tempConfigFile.toFile())) {
            writer.write("input.directory=" + tempInputDir.toString() + "\n");
            writer.write("output.directory=" + tempOutputDir.toString() + "\n");
            writer.write("diagram.types=class,sequence\n");
        }

        // Act
        configManager.loadFileConfig(tempConfigFile.toString());

        // Assert
        assertEquals(tempInputDir.toString(), configManager.getProperty("input.directory"), "File input directory should be set correctly");
        assertEquals(tempOutputDir.toString(), configManager.getProperty("output.directory"), "File output directory should be set correctly");
        assertEquals("class,sequence", configManager.getProperty("diagram.types"), "File diagram types should be set correctly");
    }

    @Test
    void testLoadFileConfig_InvalidFile() {
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            configManager.loadFileConfig("nonexistent_file.properties");
        });

        assertTrue(exception.getMessage().contains("Unable to read file: nonexistent_file.properties"),
                "Exception message should indicate file read error");
    }
}