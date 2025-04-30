package com.pjsoft.j2arch.docgen.pumldoc.util;

import com.pjsoft.j2arch.core.util.DirectoryConstants;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.core.util.StyleConstants;

/**
 * HTMLGenerationContext
 * 
 * Represents the context for generating HTML documentation for PlantUML diagrams.
 * This class provides configuration details such as input/output directories,
 * template file paths, and stylesheet paths.
 * 
 * Responsibilities:
 * - Stores paths for input and output directories.
 * - Provides paths for diagram and index HTML templates.
 * - Resolves paths for images and stylesheets in the output directory.
 * 
 * Dependencies:
 * - {@link PathResolver}: Utility for resolving file paths.
 * - {@link DirectoryConstants}: Constants for directory names.
 * - {@link StyleConstants}: Constants for stylesheet names.
 * 
 * Limitations:
 * - Assumes that the provided paths are valid and accessible.
 * - Does not validate the existence or correctness of the files at the specified paths.
 * 
 * Thread Safety:
 * - This class is immutable and therefore thread-safe.
 * 
 * Usage Example:
 * {@code
 * HTMLGenerationContext context = new HTMLGenerationContext(
 *     "/input/diagrams",
 *     "/output/docs",
 *     "/templates/diagram.html",
 *     "/templates/index.html",
 *     "/styles/main.css"
 * );
 * String inputDir = context.getInputDirectory();
 * String outputDir = context.getOutputDirectory();
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class HTMLGenerationContext {
    private final String inputDirectory; // Path to the input directory containing .puml files
    private final String outputDirectory; // Path to the output directory for generated documentation
    private final String diagramTemplateFile; // Path to the diagram HTML template file
    private final String indexTemplateFile; // Path to the index HTML template file
    private final String styleSourceFile; // Path to the source CSS file
    private final String imagesOutputDirectory; // Path to the output directory for images
    private final String styleOutputFile; // Path to the output CSS file

    /**
     * Constructs a new HTMLGenerationContext.
     * 
     * Responsibilities:
     * - Initializes paths for input/output directories, templates, and stylesheets.
     * - Resolves paths for images and stylesheets in the output directory.
     * 
     * @param inputDirectory      The directory containing input .puml files.
     * @param outputDirectory     The directory where the generated documentation will be stored.
     * @param diagramTemplateFile The path to the diagram HTML template file.
     * @param indexTemplateFile   The path to the index HTML template file.
     * @param styleSourceFile     The path to the source CSS file.
     */
    public HTMLGenerationContext(String inputDirectory, String outputDirectory, String diagramTemplateFile,
            String indexTemplateFile, String styleSourceFile) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.diagramTemplateFile = diagramTemplateFile;
        this.indexTemplateFile = indexTemplateFile;
        this.styleSourceFile = styleSourceFile;
        this.imagesOutputDirectory = PathResolver.resolvePath(outputDirectory, DirectoryConstants.IMAGES_DIR);
        this.styleOutputFile = PathResolver.resolvePath(outputDirectory, StyleConstants.OUTPUT_HTMLDOC_STYLE);
    }

    /**
     * Retrieves the input directory path.
     * 
     * @return The path to the input directory containing .puml files.
     */
    public String getInputDirectory() {
        return inputDirectory;
    }

    /**
     * Retrieves the output directory path.
     * 
     * @return The path to the output directory for generated documentation.
     */
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Retrieves the path to the diagram HTML template file.
     * 
     * @return The path to the diagram HTML template file.
     */
    public String getDiagramTemplateFile() {
        return diagramTemplateFile;
    }

    /**
     * Retrieves the path to the index HTML template file.
     * 
     * @return The path to the index HTML template file.
     */
    public String getIndexTemplateFile() {
        return indexTemplateFile;
    }

    /**
     * Retrieves the path to the source CSS file.
     * 
     * @return The path to the source CSS file.
     */
    public String getStyleSourceFile() {
        return styleSourceFile;
    }

    /**
     * Retrieves the path to the output directory for images.
     * 
     * @return The path to the output directory for images.
     */
    public String getImagesOutputDirectory() {
        return imagesOutputDirectory;
    }

    /**
     * Retrieves the path to the output CSS file.
     * 
     * @return The path to the output CSS file.
     */
    public String getStyleOutputFile() {
        return styleOutputFile;
    }
}