package com.pjsoft.j2arch.core.context;

/**
 * GenerationContext
 * 
 * Represents the context for code generation processes. This interface provides
 * methods to retrieve configuration details such as input and output directories,
 * package inclusion filters, and paths for resources like PlantUML files, images,
 * and libraries.
 * 
 * Responsibilities:
 * - Provide access to the input directory for source files.
 * - Provide access to the output directory for generated files.
 * - Define the package inclusion filter for processing specific packages.
 * - Provide paths for PlantUML files, image outputs, and library dependencies.
 * 
 * Limitations:
 * - Assumes that the paths returned by the methods are valid and accessible.
 * - Does not enforce validation of the paths or directories.
 * 
 * Usage Example:
 * {@code
 * GenerationContext context = ...; // Implementation of the interface
 * String inputDir = context.getInputDirectory();
 * String outputDir = context.getOutputDirectory();
 * System.out.println("Input Directory: " + inputDir);
 * System.out.println("Output Directory: " + outputDir);
 * }
 * 
 * Author: PJSoft
 * Version: 1.1
 * Since: 1.0
 */
public interface GenerationContext {

    /**
     * Gets the input directory for source files.
     * 
     * @return the path to the input directory.
     * @since 1.0
     */
    String getInputDirectory();

    /**
     * Gets the output directory for generated files.
     * 
     * @return the path to the output directory.
     * @since 1.0
     */
    String getOutputDirectory();

    /**
     * Gets the package inclusion filter for processing specific packages.
     * 
     * @return the package inclusion filter, or {@code null} if no filter is set.
     * @since 1.0
     */
    String getIncludePackage();

    /**
     * Gets the path to the directory for PlantUML files.
     * 
     * @return the path to the PlantUML directory.
     * @since 1.0
     */
    String getPumlPath();

    /**
     * Gets the path to the directory for image outputs.
     * 
     * @return the path to the image output directory.
     * @since 1.0
     */
    String getImagesOutputDirectory();

    /**
     * Gets the path to the directory for library dependencies.
     * 
     * @return the path to the library directory.
     * @since 1.0
     */
    String getLibsDirPath();
}
