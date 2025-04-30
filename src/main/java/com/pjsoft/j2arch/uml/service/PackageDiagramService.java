package com.pjsoft.j2arch.uml.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.uml.util.DiagramImageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for generating package-level UML diagrams.
 * 
 * Responsibilities:
 * - Generates `.puml` files for a given package.
 * - Delegates image generation to {@link DiagramImageGenerator}.
 * - Updates the {@link PackageEntity} with the path to the generated diagram.
 * 
 * Dependencies:
 * - {@link PackageEntity}: Represents a package and its associated classes.
 * - {@link JavaDocGenerationContext}: Provides configuration details for diagram generation.
 * - {@link DiagramImageGenerator}: Handles the generation of diagram images from `.puml` files.
 * 
 * Limitations:
 * - Assumes that the provided {@link PackageEntity} contains valid class information.
 * - Requires valid paths for `.puml` files and image output directories.
 * - Does not validate the correctness of the generated diagrams.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class PackageDiagramService {
    private static final Logger logger = LoggerFactory.getLogger(PackageDiagramService.class);

    /**
     * Generates a package diagram for the given {@link PackageEntity}.
     *
     * Responsibilities:
     * - Writes PlantUML syntax to a `.puml` file for the package.
     * - Generates a diagram image (e.g., `.png`) from the `.puml` file.
     * - Updates the {@link PackageEntity} with the path to the generated diagram.
     * 
     * @param packageEntity The package entity for which the diagram is to be generated.
     * @param context       The context containing paths and configuration for diagram generation.
     * @throws IOException If an error occurs while writing the `.puml` file.
     * @throws RuntimeException If an error occurs during image generation.
     */
    public void generatePackageDiagram(PackageEntity packageEntity, JavaDocGenerationContext context) throws IOException {
        logger.debug("Starting package diagram generation for package: {}", packageEntity.getName());

        // Step 1: Generate the .puml file
        String pumlFilePath = context.getPumlPath() + File.separator + packageEntity.getName().replace(".", "_") + ".puml";
        File pumlFile = new File(pumlFilePath);

        // Ensure the parent directory for the .puml file exists
        File pumlDirectory = pumlFile.getParentFile();
        if (!pumlDirectory.exists()) {
            pumlDirectory.mkdirs(); // Create the directory if it doesn't exist
        }

        try (FileWriter writer = new FileWriter(pumlFile)) {
            writer.write("@startuml\n");
            writer.write("package " + packageEntity.getName() + " {\n");
            for (CodeEntity codeEntity : packageEntity.getClasses()) {
                writer.write("    " + codeEntity.getName() + "\n");
            }
            writer.write("}\n");
            writer.write("@enduml\n");
            logger.debug("PUML file generated at: {}", pumlFilePath);
        } catch (IOException e) {
            logger.error("Error writing .puml file for package: {}", packageEntity.getName(), e);
            throw e;
        }

        // Step 2: Generate the diagram image
        String outputImageDirectory = context.getImagesOutputDirectory();
        File outputImageDir = new File(outputImageDirectory);

        DiagramImageGenerator generator = new DiagramImageGenerator();
        try {
            generator.generateDiagramImage(pumlFile, outputImageDir);
            logger.debug("Diagram image generated at: {}", outputImageDirectory);
        } catch (Exception e) {
            logger.error("Error generating diagram image for package: {}", packageEntity.getName(), e);
            throw new RuntimeException("Failed to generate diagram image for package: " + packageEntity.getName(), e);
        }

        // Step 3: Set the diagram path in the package entity
        String imageFilePath = packageEntity.getName().replace(".", "_") + ".png";
        packageEntity.setPackageDiagram(imageFilePath);
        logger.debug("Package diagram generation completed for package: {}", packageEntity.getName());
    }
}