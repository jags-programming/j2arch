package com.pjsoft.j2arch.docgen.javadoc.util;

import com.pjsoft.j2arch.core.context.GenerationContext;
import com.pjsoft.j2arch.core.util.DirectoryConstants;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.core.util.StyleConstants;

/**
 * JavaDocGenerationContext
 * 
 * Represents the context for generating Javadoc-like documentation. This class
 * encapsulates configuration details such as input/output directories, template
 * file paths, stylesheet paths, and additional options for package inclusion and
 * library paths.
 * 
 * Responsibilities:
 * - Stores paths for input and output directories.
 * - Provides paths for HTML templates and stylesheets.
 * - Resolves paths for images, PUML files, and other resources in the output directory.
 * - Stores additional configuration options like included packages and library paths.
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
 * JavaDocGenerationContext context = new JavaDocGenerationContext(
 *     "/input/src",
 *     "/output/docs",
 *     "/templates/index.html",
 *     "/templates/class.html",
 *     "/templates/package.html",
 *     "/styles/main.css",
 *     "com.example",
 *     "/libs"
 * );
 * String inputDir = context.getInputDirectory();
 * String outputDir = context.getOutputDirectory();
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocGenerationContext implements GenerationContext {
    private final String inputDirectory; // Path to the input directory containing source files
    private final String outputDirectory; // Path to the output directory for generated documentation
    private final String htmlDirectory; // Path to the HTML output directory
    private final String imagesOutputDirectory; // Path to the output directory for images
    private final String pumlPath; // Path to the directory for PUML files
    private final String indexTemplateFile; // Path to the index HTML template file
    private final String classTemplateFile; // Path to the class HTML template file
    private final String packageTemplateFile; // Path to the package HTML template file
    private final String styleSourceFile; // Path to the source CSS file
    private final String styleOutputFile; // Path to the output CSS file
    private final String includePackage; // Package to include in the documentation
    private final String libsDirPath; // Path to the directory containing library files

    /**
     * Constructs a new JavaDocGenerationContext.
     * 
     * Responsibilities:
     * - Initializes paths for input/output directories, templates, and stylesheets.
     * - Resolves paths for images, PUML files, and other resources in the output directory.
     * 
     * @param inputDirectory      The directory containing input source files.
     * @param outputDirectory     The directory where the generated documentation will be stored.
     * @param indexTemplateFile   The path to the index HTML template file.
     * @param classTemplateFile   The path to the class HTML template file.
     * @param packageTemplateFile The path to the package HTML template file.
     * @param styleSourceFile     The path to the source CSS file.
     * @param includePackage      The package to include in the documentation.
     * @param libsDirPath         The path to the directory containing library files.
     * @throws IllegalArgumentException If the input or output directory is null or empty.
     */
    public JavaDocGenerationContext(String inputDirectory, String outputDirectory, String indexTemplateFile,
            String classTemplateFile, String packageTemplateFile, String styleSourceFile,
            String includePackage, String libsDirPath) {
        if (inputDirectory == null || inputDirectory.isEmpty()) {
            throw new IllegalArgumentException("Input directory cannot be null or empty");
        }
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            throw new IllegalArgumentException("Output directory cannot be null or empty");
        }

        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.htmlDirectory = PathResolver.resolvePath(outputDirectory, DirectoryConstants.HTML_DIR);
        this.imagesOutputDirectory = PathResolver.resolvePath(this.htmlDirectory, DirectoryConstants.IMAGES_DIR);
        this.pumlPath = PathResolver.resolvePath(outputDirectory, DirectoryConstants.PUML_DIR);
        this.indexTemplateFile = indexTemplateFile;
        this.classTemplateFile = classTemplateFile;
        this.packageTemplateFile = packageTemplateFile;
        this.styleSourceFile = styleSourceFile;
        this.styleOutputFile = PathResolver.resolvePath(this.htmlDirectory, StyleConstants.OUTPUT_JAVADOC_STYLE);
        this.includePackage = includePackage;
        this.libsDirPath = libsDirPath;
    }

    /**
     * Retrieves the input directory path.
     * 
     * @return The path to the input directory containing source files.
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
     * Retrieves the HTML output directory path.
     * 
     * @return The path to the HTML output directory.
     */
    public String getHtmlDirectory() {
        return htmlDirectory;
    }

    /**
     * Retrieves the path to the output CSS file.
     * 
     * @return The path to the output CSS file.
     */
    public String getStyleOutputFile() {
        return styleOutputFile;
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
     * Retrieves the path to the directory for PUML files.
     * 
     * @return The path to the directory for PUML files.
     */
    public String getPumlPath() {
        return pumlPath;
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
     * Retrieves the path to the class HTML template file.
     * 
     * @return The path to the class HTML template file.
     */
    public String getClassTemplateFile() {
        return classTemplateFile;
    }

    /**
     * Retrieves the path to the package HTML template file.
     * 
     * @return The path to the package HTML template file.
     */
    public String getPackageTemplateFile() {
        return packageTemplateFile;
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
     * Retrieves the package to include in the documentation.
     * 
     * @return The package to include in the documentation.
     */
    public String getIncludePackage() {
        return includePackage;
    }

    /**
     * Retrieves the path to the directory containing library files.
     * 
     * @return The path to the directory containing library files.
     */
    public String getLibsDirPath() {
        return libsDirPath;
    }
}