package com.pjsoft.j2arch.uml.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.config.SymbolSolverConfig;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.util.JavaParserService;

/**
 * Analyzes a Java project to extract information for UML diagram generation.
 * 
 * This class is responsible for configuring the symbol solver, collecting `.java` files
 * from the input directory, and parsing the files to extract code entities. It acts as
 * a bridge between the configuration and the parsing services.
 * 
 * Responsibilities:
 * - Configures the symbol solver for type resolution.
 * - Recursively collects `.java` files from the input directory.
 * - Filters classes based on the "include.package" configuration property.
 * - Parses the collected files to extract code entities.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = new ConfigurationManager("config.properties");
 * ProjectAnalyzer analyzer = new ProjectAnalyzer(config);
 * analyzer.configureSymbolSolver();
 * List<CodeEntity> entities = analyzer.analyzeProject();
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}
 * - {@link JavaParserService}
 * - {@link SymbolSolverConfig}
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Limitations:
 * - Assumes that the input directory is valid and contains `.java` files.
 * - Requires a valid configuration file with the input directory specified.
 * - Filters classes based on the "include.package" configuration property.
 * 
 * @author PJSoft
 * @version 1.1
 * @since 1.0
 */
public class ProjectAnalyzer {
    private final ConfigurationManager config;
    private final JavaParserService parser;

    /**
     * Constructs a new ProjectAnalyzer with the specified configuration manager.
     * 
     * Responsibilities:
     * - Initializes the configuration manager and the JavaParserService.
     * 
     * @param config the configuration manager containing project settings.
     * @since 1.0
     */
    public ProjectAnalyzer(ConfigurationManager config) {
        this.config = config;
        this.parser = new JavaParserService(config);
    }

    /**
     * Configures the symbol solver with the input directory specified in the configuration.
     * 
     * Responsibilities:
     * - Reads the input directory path from the configuration.
     * - Configures the symbol solver for type resolution using the {@link SymbolSolverConfig}.
     * 
     * Preconditions:
     * - The input directory must be specified in the configuration.
     * 
     * Postconditions:
     * - The symbol solver is configured for type resolution.
     * 
     * @throws IllegalArgumentException if the input directory is not specified or invalid.
     * @since 1.0
     */
    public void configureSymbolSolver() {
        String inputSourceRootPath = config.getProperty("input.directory");
        if (inputSourceRootPath == null || inputSourceRootPath.isEmpty()) {
            throw new IllegalArgumentException("Input directory is not specified in the configuration.");
        }
        SymbolSolverConfig.configureSymbolSolver(inputSourceRootPath);
    }

    /**
     * Recursively collects all `.java` files from the input directory specified in the configuration.
     * 
     * Responsibilities:
     * - Validates the input directory.
     * - Recursively searches for `.java` files in the directory and its subdirectories.
     * 
     * Preconditions:
     * - The input directory must be specified in the configuration.
     * 
     * Postconditions:
     * - A list of `.java` file paths is returned.
     * 
     * @return a list of file paths for all `.java` files.
     * @throws IllegalArgumentException if the input directory is not specified or invalid.
     * @since 1.0
     */
    public List<String> collectJavaFiles() {
        String directoryPath = config.getProperty("input.directory");
        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new IllegalArgumentException("Input directory is not specified in the configuration.");
        }

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Input directory does not exist or is not a directory: " + directoryPath);
        }

        List<String> files = new ArrayList<>();
        collectJavaFiles(directory, files);
        return files;
    }

    /**
     * Recursively collects `.java` files from the specified directory.
     * 
     * Responsibilities:
     * - Traverses the directory structure to find `.java` files.
     * 
     * @param directory the directory to search.
     * @param files the list to store the file paths.
     * @since 1.0
     */
    private void collectJavaFiles(File directory, List<String> files) {
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    collectJavaFiles(file, files); // Recurse into subdirectories
                } else if (file.getName().endsWith(".java")) {
                    files.add(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Parses the collected `.java` files and extracts {@link CodeEntity} objects.
     * 
     * Responsibilities:
     * - Collects `.java` files from the input directory.
     * - Parses the files using the {@link JavaParserService}.
     * 
     * Preconditions:
     * - The input directory must be valid and contain `.java` files.
     * 
     * Postconditions:
     * - A list of {@link CodeEntity} objects is returned, representing parsed classes.
     * 
     * @return a list of {@link CodeEntity} objects representing parsed classes.
     * @throws IllegalArgumentException if the input directory is invalid or contains no `.java` files.
     * @since 1.0
     */
    public List<CodeEntity> analyzeProject() {
        // Collect Java files from the input directory
        List<String> files = collectJavaFiles();

        // Parse the files and return the extracted CodeEntity objects
        return parser.parseFiles(files);
    }
}
