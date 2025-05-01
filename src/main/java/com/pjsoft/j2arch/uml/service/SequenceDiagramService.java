package com.pjsoft.j2arch.uml.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.pjsoft.j2arch.uml.util.DiagramImageGenerator;
import com.pjsoft.j2arch.uml.util.ScenarioBuilder;

import com.pjsoft.j2arch.core.context.GenerationContext;
import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.WorkUnitType;
import com.pjsoft.j2arch.uml.model.Scenario;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * SequenceDiagramService
 * 
 * This service is responsible for generating sequence diagrams from the provided
 * CodeEntity objects. It handles the creation of PlantUML `.puml` files and the
 * generation of diagram images (e.g., `.png` or `.svg`) using the PlantUML library.
 * 
 * Responsibilities:
 * - Identifies scenarios using the {@link ScenarioBuilder}.
 * - Writes PlantUML syntax to `.puml` files for each scenario.
 * - Generates diagram images (e.g., `.png`) from the `.puml` files.
 * - Manages output directories for `.puml` files and generated images.
 * 
 * Dependencies:
 * - {@link ScenarioBuilder}: Used to identify scenarios for sequence diagrams.
 * - {@link DiagramImageGenerator}: Used to generate diagram images from `.puml` files.
 * - {@link GenerationContext}: Provides configuration details for diagram generation.
 * 
 * Limitations:
 * - Assumes that the provided CodeEntity objects are complete and accurate.
 * - Requires valid output directories for `.puml` files and images.
 * - Does not validate the correctness of the generated diagrams.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class SequenceDiagramService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SequenceDiagramService.class);

    /**
     * Constructs a SequenceDiagramService.
     * 
     * Responsibilities:
     * - Initializes the service for sequence diagram generation.
     */
    public SequenceDiagramService() {
        // No explicit initialization required for now
    }

    /**
     * Generates sequence diagrams based on the provided CodeEntity objects.
     * 
     * This method performs the following steps:
     * 1. Identifies scenarios using the {@link ScenarioBuilder}.
     * 2. Writes PlantUML syntax to `.puml` files for each scenario.
     * 3. Generates diagram images (e.g., `.png`) from the `.puml` files.
     * 
     * @param codeEntities The list of CodeEntity objects representing the parsed classes.
     * @param context      The generation context containing configuration details.
     * @return The path to the output directory containing the generated diagrams.
     * @throws IllegalArgumentException If no CodeEntity objects are provided.
     */
    public String generateSequenceDiagram(List<CodeEntity> codeEntities, GenerationContext context,ProgressTracker progressTracker) {
        if (codeEntities.isEmpty()) {
            throw new IllegalArgumentException("No code entities provided for generating the sequence diagram. Please check input or configuration property.");
        }
        logger.debug("Generating sequence diagram...");

        // Step 1: Retrieve the output directory for `.puml` files
        String outputDirectoryName = context.getPumlPath();
        File outputDirectory = new File(outputDirectoryName);

        // Step 2: Generate scenarios using ScenarioBuilder
        ScenarioBuilder scenarioBuilder = new ScenarioBuilder(context.getIncludePackage());
        List<Scenario> scenarios = scenarioBuilder.getScenarios(codeEntities);
        
        /// Update progress tracker
        progressTracker.addTotalUnits(WorkUnitType.SEQUENCE_DIAGRAM, scenarios.size());
        // Step 3: Retrieve the output directory for images
        String imageOutputDir = context.getImagesOutputDirectory();
        File imageOutputDirectory = new File(imageOutputDir);

        // Step 4: Generate PlantUML syntax and write `.puml` files
        for (Scenario scenario : scenarios) {
            try {
                String plantUmlSyntax = scenario.toPlantUmlSyntax(); // Get PlantUML syntax from Scenario
                String pumlFilePath = writePlantUmlToFile(plantUmlSyntax, outputDirectory, scenario);

                // Step 5: Generate diagram images from `.puml` files
                DiagramImageGenerator imageGenerator = new DiagramImageGenerator();
                File pumlFile = new File(pumlFilePath);
                imageGenerator.generateDiagramImage(pumlFile, imageOutputDirectory);
            } catch (IOException e) {
                logger.error("Failed to process scenario: " + scenario.getEntryClass(), e);
            }

            progressTracker.addCompletedUnits(WorkUnitType.SEQUENCE_DIAGRAM, 1);
        }

        logger.debug("Sequence diagram generation completed.");
        return outputDirectoryName; // Return the output directory path
    }

    /**
     * Writes the PlantUML syntax to a `.puml` file.
     * 
     * This method ensures that the output directory exists before writing the `.puml` file.
     * 
     * @param plantUmlSyntax  The PlantUML syntax to write.
     * @param outputDirectory The directory to write the `.puml` file to.
     * @param scenario        The scenario for which the `.puml` file is being created.
     * @return The full path to the created `.puml` file.
     * @throws IOException If an error occurs while writing the file.
     */
    private String writePlantUmlToFile(String plantUmlSyntax, File outputDirectory, Scenario scenario) throws IOException {
        // Ensure the output directory exists
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDirectory);
            }
        }

        // Create the `.puml` file
        String fileName = scenario.getEntryClass() + "_" + scenario.getStartingMethod();
        File pumlFile = new File(outputDirectory, fileName + ".puml");
        try (FileWriter writer = new FileWriter(pumlFile)) {
            // Write the PlantUML syntax to the file
            writer.write(plantUmlSyntax);
            logger.debug("Successfully wrote PlantUML file: " + pumlFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to write PlantUML file: " + pumlFile.getAbsolutePath(), e);
            throw e; // Rethrow the exception to handle it in the calling method
        }

        return pumlFile.getAbsolutePath(); // Return the full path to the `.puml` file
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
                logger.debug("Generated diagram image: " + image.getPngFile().getAbsolutePath());
            }

            logger.debug("Diagram image generated successfully for: " + pumlFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Error generating diagram image for: " + pumlFilePath, e);
        }
    }
}