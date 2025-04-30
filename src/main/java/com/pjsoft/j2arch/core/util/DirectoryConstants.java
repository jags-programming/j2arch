package com.pjsoft.j2arch.core.util;

/**
 * DirectoryConstants
 * 
 * Defines constants for commonly used directory names and default paths in the application.
 * These constants are used throughout the application to standardize directory structures
 * for input, output, and resource files.
 * 
 * Responsibilities:
 * - Provide standard directory names for various resources (e.g., PUML, images, styles).
 * - Define default paths for input and output directories.
 * - Ensure consistency in directory naming conventions across the application.
 * 
 * Limitations:
 * - Assumes that the specified directories exist or are created during runtime.
 * - Does not validate the existence of the directories.
 * 
 * Usage Example:
 * {@code
 * String inputDir = DirectoryConstants.DEFAULT_INPUT_DIR;
 * String outputDir = DirectoryConstants.DEFAULT_OUTPUT_UMLDOC_DIR;
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class DirectoryConstants {

    // Common directory names

    /** Directory for PlantUML files. */
    public static final String PUML_DIR = "puml";

    /** Directory for image files. */
    public static final String IMAGES_DIR = "images";

    /** Directory for stylesheet files. */
    public static final String STYLES_DIR = "styles";

    /** Directory for HTML files. */
    public static final String HTML_DIR = "html";

    /** Directory for external library files. */
    public static final String LIBS_DIR = "libs";

    // Default paths

    /** Default input directory for source files. */
    public static final String DEFAULT_INPUT_DIR = "./input";

    /** Default output directory for UML documentation. */
    public static final String DEFAULT_OUTPUT_UMLDOC_DIR = "./umldoc";

    /** Default output directory for Javadoc documentation. */
    public static final String DEFAULT_OUTPUT_JAVADOC_DIR = "./javadoc";

    /** Default output directory for HTML documentation. */
    public static final String DEFAULT_OUTPUT_HTMLDOC_DIR = "./htmldoc";

    /** Default filename for the unified class diagram. */
    public static final String DEFAULT_UNIFIED_CLASS_DIAGRAM = "UnifiedClassDiagram.puml";
}
