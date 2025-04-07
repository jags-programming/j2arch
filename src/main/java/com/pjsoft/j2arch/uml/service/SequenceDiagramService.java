package com.pjsoft.j2arch.uml.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.pjsoft.j2arch.uml.util.ScenarioBuilder;
import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.model.Scenario;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * SequenceDiagramService
 * 
 * This service is responsible for generating sequence diagrams from the provided
 * CodeEntity objects. It handles the creation of PlantUML `.puml` files and the
 * generation of diagram images (e.g., `.png` or `.svg`) using the PlantUML library.
 */
public class SequenceDiagramService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SequenceDiagramService.class);
    private final ConfigurationManager config;

    /**
     * Constructs a SequenceDiagramService with the specified configuration manager.
     * 
     * @param config The configuration manager containing project-specific settings.
     */
    public SequenceDiagramService(ConfigurationManager config) {
        this.config = config;
    }

    /**
     * Generates sequence diagrams based on the provided CodeEntity objects.
     * 
     * This method performs the following steps:
     * 1. Identifies scenarios using the ScenarioBuilder.
     * 2. Writes PlantUML syntax to `.puml` files for each scenario.
     * 3. Generates diagram images (e.g., `.png`) from the `.puml` files.
     * 
     * @param codeEntities The list of CodeEntity objects representing the parsed classes.
     * @return The path to the output directory containing the generated diagrams.
     */
    public String generateSequenceDiagram(List<CodeEntity> codeEntities) {
        logger.info("Generating sequence diagram...");

        // Step 1: Retrieve the output directory
        String outputDirectory = config.getProperty("output.directory");

        // Step 2: Generate scenarios using ScenarioBuilder
        ScenarioBuilder scenarioBuilder = new ScenarioBuilder(config.getProperty("project.package"));
        List<Scenario> scenarios = scenarioBuilder.getScenarios(codeEntities);

        // Step 3: Generate PlantUML syntax and write .puml files
        for (Scenario scenario : scenarios) {
            try {
                String plantUmlSyntax = scenario.toPlantUmlSyntax(); // Get PlantUML syntax from Scenario
                String pumlFilePath = writePlantUmlToFile(plantUmlSyntax, outputDirectory, scenario.getEntryClass());

                // Step 4: Generate diagram images from .puml files
                generateDiagramImage(pumlFilePath);
            } catch (IOException e) {
                logger.error("Failed to process scenario: " + scenario.getEntryClass(), e);
            }
        }

        logger.info("Sequence diagram generation completed.");
        return outputDirectory; // Return the output directory path
    }

    /**
     * Writes the PlantUML syntax to a `.puml` file.
     * 
     * This method ensures that the output directory exists before writing the `.puml` file.
     * 
     * @param plantUmlSyntax The PlantUML syntax to write.
     * @param outputDirectory The directory to write the `.puml` file to.
     * @param fileName The name of the `.puml` file (without extension).
     * @return The full path to the created `.puml` file.
     * @throws IOException If an error occurs while writing the file.
     */
    private String writePlantUmlToFile(String plantUmlSyntax, String outputDirectory, String fileName) throws IOException {
        // Ensure the output directory exists
        File directory = new File(outputDirectory);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDirectory);
            }
        }

        // Create the .puml file
        File pumlFile = new File(directory, fileName + ".puml");
        try (FileWriter writer = new FileWriter(pumlFile)) {
            // Write the PlantUML syntax to the file
            writer.write(plantUmlSyntax);
            logger.info("Successfully wrote PlantUML file: " + pumlFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to write PlantUML file: " + pumlFile.getAbsolutePath(), e);
            throw e; // Rethrow the exception to handle it in the calling method
        }

        return pumlFile.getAbsolutePath(); // Return the full path to the .puml file
    }

    /**
     * Generates a diagram image (e.g., `.png` or `.svg`) from a `.puml` file.
     * 
     * This method uses the PlantUML library's SourceFileReader to generate the diagram
     * image. If the `.puml` file does not exist or no images are generated, an exception
     * is thrown.
     * 
     * @param pumlFilePath The full path to the `.puml` file.
     * @throws RuntimeException If an error occurs while generating the diagram image.
     */
    private void generateDiagramImage(String pumlFilePath) {
        try {
            File pumlFile = new File(pumlFilePath);
            if (!pumlFile.exists()) {
                throw new IllegalArgumentException("PUML file not found at: " + pumlFilePath);
            }

            // Use PlantUML's SourceFileReader to generate the diagram
            SourceFileReader reader = new SourceFileReader(pumlFile);
            List<GeneratedImage> generatedImages = reader.getGeneratedImages();

            if (generatedImages.isEmpty()) {
                throw new RuntimeException("No diagram images were generated for: " + pumlFilePath);
            }

            for (GeneratedImage image : generatedImages) {
                logger.info("Generated diagram image: " + image.getPngFile().getAbsolutePath());
            }

            logger.info("Diagram image generated successfully for: " + pumlFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Error generating diagram image for: " + pumlFilePath, e);
        }
    }
}