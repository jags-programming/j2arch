package com.pjsoft.j2arch.uml;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.model.MethodEntity;
import com.pjsoft.j2arch.uml.service.SequenceDiagramService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SequenceDiagramService} class.
 */
class SequenceDiagramServiceTest {

    private static final String OUTPUT_DIRECTORY = "./output";

    @BeforeEach
    void setUp() {
        // Ensure the output directory exists before each test
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up the output directory after each test
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (outputDir.exists()) {
            for (File file : outputDir.listFiles()) {
                file.delete();
            }
            outputDir.delete();
        }
    }

    /**
     * Tests the `generateSequenceDiagram` method with valid input.
     */
    @Test
    void testGenerateSequenceDiagram_ValidInput() throws IOException {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.loadDefaultConfig(); // Load default configuration

        SequenceDiagramService service = new SequenceDiagramService(configManager);

        List<CodeEntity> codeEntities = new ArrayList<>();
        CodeEntity classA = new CodeEntity("ClassA");
        MethodEntity methodA = new MethodEntity("methodA", "void");
        classA.addMethod(methodA);
        codeEntities.add(classA);

        // Act
        String outputDirectory = service.generateSequenceDiagram(codeEntities);

        // Assert
        assertEquals(OUTPUT_DIRECTORY, outputDirectory, "The output directory should match the default configuration");
        File pumlFile = new File(OUTPUT_DIRECTORY + "/ClassA.puml");
        assertTrue(pumlFile.exists(), "The .puml file should be created");
    }

    /**
     * Tests the `generateSequenceDiagram` method with an empty input list.
     */
    @Test
    void testGenerateSequenceDiagram_EmptyInput() throws IOException {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.loadDefaultConfig(); // Load default configuration

        SequenceDiagramService service = new SequenceDiagramService(configManager);

        // Act
        String outputDirectory = service.generateSequenceDiagram(new ArrayList<>());

        // Assert
        assertEquals(OUTPUT_DIRECTORY, outputDirectory, "The output directory should match the default configuration");
        File outputDir = new File(OUTPUT_DIRECTORY);
        assertEquals(0, outputDir.listFiles().length, "No files should be created for an empty input list");
    }

    /**
     * Tests the `generateSequenceDiagram` method to ensure it creates .puml files.
     */
    @Test
    void testGenerateSequenceDiagram_CreatesPumlFiles() throws IOException {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.loadDefaultConfig(); // Load default configuration

        SequenceDiagramService service = new SequenceDiagramService(configManager);

        List<CodeEntity> codeEntities = new ArrayList<>();
        CodeEntity classA = new CodeEntity("ClassA");
        MethodEntity methodA = new MethodEntity("methodA", "void");
        classA.addMethod(methodA);
        codeEntities.add(classA);

        // Act
        service.generateSequenceDiagram(codeEntities);

        // Assert
        File pumlFile = new File(OUTPUT_DIRECTORY + "/ClassA.puml");
        assertTrue(pumlFile.exists(), "The .puml file should be created");
    }

    /**
     * Tests the `generateSequenceDiagram` method to ensure it creates diagram images.
     */
    @Test
    void testGenerateSequenceDiagram_CreatesDiagramImages() throws IOException {
        // Arrange
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.loadDefaultConfig(); // Load default configuration

        SequenceDiagramService service = new SequenceDiagramService(configManager);

        List<CodeEntity> codeEntities = new ArrayList<>();
        CodeEntity classA = new CodeEntity("ClassA");
        MethodEntity methodA = new MethodEntity("methodA", "void");
        classA.addMethod(methodA);
        codeEntities.add(classA);

        // Act
        service.generateSequenceDiagram(codeEntities);

        // Assert
        File imageFile = new File(OUTPUT_DIRECTORY + "/ClassA.png");
        assertTrue(imageFile.exists(), "The diagram image should be created");
    }

}