package com.pjsoft.j2arch.uml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UMLDiagramGeneratorTest {

    private ConfigurationManager configManager;
    private UMLDiagramGenerator generator;
    private Path tempInputDir;
    private Path tempOutputDir;

    @BeforeEach
    void setUp() throws IOException {
        // Set up a temporary input directory
        tempInputDir = Files.createTempDirectory("input");
        tempOutputDir = Files.createTempDirectory("output");

        // Write a sample `.java` file to the input directory
        File sampleJavaFile = new File(tempInputDir.toFile(), "Sample.java");
        try (FileWriter writer = new FileWriter(sampleJavaFile)) {
            writer.write("public class Sample {}");
        }

        // Set up the configuration manager
        configManager = ConfigurationManager.getInstance();
        configManager.clearSettings();
        configManager.loadDefaultConfig();
        configManager.setProperty("input.directory", tempInputDir.toString());
        configManager.setProperty("output.directory", tempOutputDir.toString());
        configManager.setProperty("diagram.types", "class,sequence");

        // Initialize the UMLDiagramGenerator
        generator = new UMLDiagramGenerator(configManager);
    }

    @Test
    void testGenerateDiagrams_Success() {
        // Act
        generator.generateDiagrams();

        // Assert
        File outputDir = new File(configManager.getProperty("output.directory"));
        assertTrue(outputDir.exists(), "Output directory should exist after diagram generation");
        assertTrue(outputDir.listFiles().length > 0, "Output directory should contain generated diagrams");
    }

    @Test
    void testValidateInputDirectory_ValidDirectory() {
        // Act & Assert
        assertDoesNotThrow(() -> generator.generateDiagrams(), "Valid input directory should not throw an exception");
    }

    @Test
    void testValidateInputDirectory_InvalidDirectory() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.setProperty("input.directory", "nonexistent_directory");
        });

        assertTrue(exception.getMessage().contains("input.directory must be a valid directory: nonexistent_directory"),
                "Exception message should indicate invalid input directory");
    }

    @Test
    void testValidateInputDirectory_NoJavaFiles() throws IOException {
        // Arrange
        Path emptyDir = Files.createTempDirectory("empty");
        configManager.setProperty("input.directory", emptyDir.toString());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateDiagrams();
        });

        assertTrue(exception.getMessage().contains("No input files found in the input directory or its subdirectories:"),
                "Exception message should indicate no `.java` files found");
    }

    @Test
    void testGenerateDiagrams_NoDiagramTypesSpecified() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.setProperty("diagram.types", "");
        });

        assertTrue(exception.getMessage().contains("Invalid diagram type: "),
                "Exception message should indicate missing diagram types");
    }

    @Test
    void testGenerateDiagrams_InvalidDiagramType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configManager.setProperty("diagram.types", "invalidType");
        });

        assertTrue(exception.getMessage().contains("Invalid diagram type: invalidType"),
                "Exception message should indicate the invalid diagram type");
    }
}