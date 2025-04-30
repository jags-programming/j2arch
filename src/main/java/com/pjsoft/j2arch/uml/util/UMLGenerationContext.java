package com.pjsoft.j2arch.uml.util;

import com.pjsoft.j2arch.core.context.GenerationContext;

/**
 * Represents the context for UML diagram generation.
 * 
 * Responsibilities:
 * - Stores configuration details required for generating UML diagrams.
 * - Provides access to input and output directories, diagram types, and other related settings.
 * 
 * Dependencies:
 * - Implements {@link GenerationContext}, which defines the basic structure for generation contexts.
 * 
 * Limitations:
 * - Assumes that all required fields are provided during initialization.
 * - Does not perform validation of the provided paths or settings.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class UMLGenerationContext implements GenerationContext {
    private final String inputDirectory;          // Path to the input directory containing source files
    private final String outputDirectory;         // Path to the output directory for generated diagrams
    private final String diagramTypes;            // Comma-separated list of diagram types to generate (e.g., "class,sequence")
    private final String includePackage;          // Package to include in the diagram generation
    private final String pumlPath;                // Path to the PlantUML `.puml` file template
    private final String imagesOutputDirectory;   // Directory for storing generated image files
    private final String unifiedClassDiagram;     // Path for the unified class diagram output
    private final String libsDirPath;             // Path to the directory containing library dependencies

    /**
     * Constructs a new UMLGenerationContext with the specified configuration details.
     * 
     * @param inputDirectory        The input directory containing source files.
     * @param outputDirectory       The output directory for generated diagrams.
     * @param diagramTypes          Comma-separated list of diagram types to generate (e.g., "class,sequence").
     * @param includePackage        The package to include in the diagram generation.
     * @param pumlPath              The path to the PlantUML `.puml` file template.
     * @param imagesOutputDirectory The directory for storing generated image files.
     * @param unifiedClassDiagram   The path for the unified class diagram output.
     * @param libsDirPath           The path to the directory containing library dependencies.
     */
    public UMLGenerationContext(String inputDirectory, String outputDirectory, String diagramTypes,
            String includePackage, String pumlPath, String imagesOutputDirectory,
            String unifiedClassDiagram, String libsDirPath) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.diagramTypes = diagramTypes;
        this.includePackage = includePackage;
        this.pumlPath = pumlPath;
        this.imagesOutputDirectory = imagesOutputDirectory;
        this.unifiedClassDiagram = unifiedClassDiagram;
        this.libsDirPath = libsDirPath;
    }

    /**
     * Gets the input directory containing source files.
     * 
     * @return The input directory path.
     */
    public String getInputDirectory() {
        return inputDirectory;
    }

    /**
     * Gets the output directory for generated diagrams.
     * 
     * @return The output directory path.
     */
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Gets the types of diagrams to generate.
     * 
     * @return A comma-separated list of diagram types (e.g., "class,sequence").
     */
    public String getDiagramTypes() {
        return diagramTypes;
    }

    /**
     * Gets the package to include in the diagram generation.
     * 
     * @return The package name to include.
     */
    public String getIncludePackage() {
        return includePackage;
    }

    /**
     * Gets the path to the PlantUML `.puml` file template.
     * 
     * @return The `.puml` file template path.
     */
    public String getPumlPath() {
        return pumlPath;
    }

    /**
     * Gets the directory for storing generated image files.
     * 
     * @return The images output directory path.
     */
    public String getImagesOutputDirectory() {
        return imagesOutputDirectory;
    }

    /**
     * Gets the path for the unified class diagram output.
     * 
     * @return The unified class diagram output path.
     */
    public String getUnifiedClassDiagram() {
        return unifiedClassDiagram;
    }

    /**
     * Gets the path to the directory containing library dependencies.
     * 
     * @return The library dependencies directory path.
     */
    public String getLibsDirPath() {
        return libsDirPath;
    }
}