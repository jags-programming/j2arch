package com.pjsoft.j2arch.uml.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.pjsoft.j2arch.uml.service.ClassDiagramService;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * Utility class for generating UML diagrams from `.puml` files using PlantUML.
 * 
 * Responsibilities:
 * - Validates `.puml` files to ensure they exist and are valid.
 * - Ensures output directories exist and are writable.
 * - Generates UML diagrams from `.puml` files using the PlantUML library.
 * - Moves generated images to the specified output directory.
 * - Logs errors and progress during the diagram generation process.
 * 
 * Limitations:
 * - Assumes that the `.puml` files are valid and properly formatted.
 * - Requires the PlantUML library for diagram generation.
 * - Does not handle advanced file system operations (e.g., symbolic links).
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Dependencies:
 * - {@link PlantUML}: Used for generating UML diagrams.
 * - {@link SourceFileReader}: Reads `.puml` files and generates images.
 * - {@link GeneratedImage}: Represents the generated diagram images.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class DiagramImageGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiagramImageGenerator.class);

    /**
     * Generates a diagram image from a `.puml` file and moves it to the specified
     * output directory.
     *
     * Responsibilities:
     * - Validates the `.puml` file to ensure it exists and is valid.
     * - Ensures the output directory exists and is writable.
     * - Generates diagram images using PlantUML.
     * - Moves the generated images to the output directory.
     *
     * @param pumlFile    The `.puml` file to be processed.
     * @param outputDir   The directory where the generated images will be moved.
     * @throws IllegalArgumentException If the `.puml` file is invalid or does not
     *                                  exist.
     * @throws RuntimeException         If the output directory cannot be created or
     *                                  is not writable, or if an error occurs during
     *                                  image generation or movement.
     */
    public void generateDiagramImage(File pumlFile, File outputDir) {
               
        try {
           
            // Validate the input .puml file
            validatePumlFile(pumlFile);
            
            // Ensure the output directory exists and is writable
            ensureDirectoryExists(outputDir);
            
            // Generate the diagram images
            List<GeneratedImage> generatedImages = generateImages(pumlFile);

            // Move the generated images to the output directory
            moveGeneratedImages(generatedImages, outputDir);

            logger.debug("Diagram generation completed successfully for: {}", pumlFile);
        } catch (Exception e) {
            logger.error("Error during diagram generation for: {}", pumlFile, e);
            // Log the problematic .puml content for debugging
            //logPumlContentForDebugging(pumlFile); // Added for debugging problematic .puml files
            throw new RuntimeException("Failed to generate diagram for file: " + pumlFile, e);
        }
    }

    /**
     * Logs the content of a `.puml` file for debugging purposes.
     *
     * @param pumlFile The `.puml` file to log.
     */
    private void logPumlContentForDebugging(File pumlFile) {
        try {
            if (!pumlFile.exists()) {
                logger.error("PUML file does not exist: {}", pumlFile.getAbsolutePath());
                return;
            }
            String pumlContent = Files.readString(pumlFile.toPath());
            logger.debug("PUML Content>>>:\n{}", pumlContent); // Logs the content of the problematic .puml file
        } catch (IOException e) {
            logger.error("Failed to read PUML content for debugging: {}", pumlFile.getAbsolutePath(), e);
        }
    }

    /**
     * Validates the existence and validity of a `.puml` file.
     *
     * Responsibilities:
     * - Checks if the file exists and is a valid file.
     * - Logs an error and throws an exception if the file is invalid.
     *
     * @param pumlFile The `.puml` file to validate.
     * @throws IllegalArgumentException If the file does not exist or is not a valid
     *                                  file.
     */
    private void validatePumlFile(File pumlFile) {
        if (!pumlFile.exists() || !pumlFile.isFile()) {
            String errorMessage = "PUML file not found or is not a valid file: " + pumlFile.getAbsolutePath();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Ensures that the specified directory exists and is writable.
     *
     * Responsibilities:
     * - Creates the directory if it does not exist.
     * - Validates that the directory is writable.
     *
     * @param directory The directory to validate or create.
     * @throws RuntimeException If the directory cannot be created or is not
     *                          writable.
     */
    private void ensureDirectoryExists(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            String errorMessage = "Failed to create directory: " + directory.getAbsolutePath();
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        if (!directory.canWrite()) {
            String errorMessage = "Output directory is not writable: " + directory.getAbsolutePath();
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Moves the generated diagram images to the specified output directory.
     *
     * Responsibilities:
     * - Iterates through the list of generated images.
     * - Moves each image to the output directory.
     * - Logs the success or failure of each move operation.
     *
     * @param generatedImages The list of generated images to move.
     * @param outputDir       The directory where the images will be moved.
     * @throws RuntimeException If any image cannot be moved to the output
     *                          directory.
     */
    private void moveGeneratedImages(List<GeneratedImage> generatedImages, File outputDir) {
        for (GeneratedImage image : generatedImages) {
            File generatedImageFile = image.getPngFile();
            File targetFile = new File(outputDir, generatedImageFile.getName());

            try {
                // Use Files.move to overwrite the target file if it exists
                Files.move(generatedImageFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (SecurityException se) {
                String errorMessage = "Permission denied while moving file from " + generatedImageFile.getAbsolutePath()
                        + " to " + targetFile.getAbsolutePath();
                logger.error(errorMessage, se);
                throw new RuntimeException(errorMessage, se);
            } catch (IOException ioe) {
                String errorMessage = "I/O error while moving file from " + generatedImageFile.getAbsolutePath()
                        + " to " + targetFile.getAbsolutePath();
                logger.error(errorMessage, ioe);
                throw new RuntimeException(errorMessage, ioe);
            }
        }
    }

    /**
     * Generates diagram images from a `.puml` file using PlantUML.
     *
     * Responsibilities:
     * - Reads the `.puml` file and generates diagram images.
     * - Validates that at least one image is generated.
     *
     * @param pumlFile The `.puml` file to process.
     * @return A list of {@link GeneratedImage} objects representing the generated
     *         images.
     * @throws RuntimeException If no images are generated or if an error occurs
     *                          during generation.
     */
    private List<GeneratedImage> generateImages(File pumlFile) {
        try {
            SourceFileReader reader = new SourceFileReader(pumlFile);
            List<GeneratedImage> generatedImages = reader.getGeneratedImages();
            if (generatedImages.isEmpty()) {
                String errorMessage = "No diagram images were generated for: " + pumlFile.getAbsolutePath();
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            return generatedImages;
        } catch (IOException e) {
            logger.error("Error generating diagram image for: {}", pumlFile.getAbsolutePath(), e);
            throw new RuntimeException("Error generating diagram image for: " + pumlFile.getAbsolutePath(), e);
        } catch(Exception e){
            logger.debug("Error generating image for puml file: {}", pumlFile);
            throw new RuntimeException("An error occured generating image for puml file" +pumlFile );
        }
    }
}
