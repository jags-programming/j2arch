package com.pjsoft.j2arch.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.docgen.HTMLDocumentationGenerator;
import com.pjsoft.j2arch.uml.util.ProjectAnalyzer;
import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.service.ClassDiagramService;
import com.pjsoft.j2arch.uml.service.SequenceDiagramService;
import com.pjsoft.j2arch.uml.service.StorageService;

/**
 * Coordinates the generation of UML diagrams (class and sequence diagrams).
 * 
 * This class interacts with various services such as {@link ProjectAnalyzer},
 * {@link ClassDiagramService}, {@link SequenceDiagramService}, and {@link StorageService}
 * to analyze the project, generate UML diagrams, and store them.
 * 
 * Responsibilities:
 * - Validates the input directory and ensures it contains valid `.java` files.
 * - Analyzes the project to extract parsed data.
 * - Generates class and sequence diagrams based on the configuration.
 * - Stores the generated diagrams in the specified storage location.
 *  * Supported Diagram Types:
 * - Class diagrams
 * - Sequence diagrams
 * Usage Example:
 * {@code
 * ConfigurationManager config = new ConfigurationManager("config.properties");
 * UMLDiagramGenerator generator = new UMLDiagramGenerator(config);
 * generator.generateDiagrams();
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}
 * - {@link ProjectAnalyzer}
 * - {@link ClassDiagramService}
 * - {@link SequenceDiagramService}
 * - {@link StorageService}
 * 
 * Thread Safety:
 * - This class is not thread-safe as it maintains state through its instance variables.
 * 
 * Limitations:
 * - Assumes that the input directory contains valid `.java` files.
 * - Requires a valid configuration file with diagram types specified.
 * 
 * @author PJSoft
 * @version 2.0
 * @since 1.0
 */
public class UMLDiagramGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UMLDiagramGenerator.class);
    private final ConfigurationManager configurationManager;
    private final ProjectAnalyzer projectAnalyzer;
    private final ClassDiagramService classDiagramService;
    private final SequenceDiagramService sequenceDiagramService;
    private final StorageService storageService;

    /**
     * Constructs a new UMLDiagramGenerator with the specified configuration manager.
     * 
     * Responsibilities:
     * - Initializes the required services for project analysis, diagram generation, and storage.
     * 
     * @param configurationManager the configuration manager containing project settings.
     * @since 1.0
     */
    public UMLDiagramGenerator(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
        this.projectAnalyzer = new ProjectAnalyzer(configurationManager);
        this.classDiagramService = new ClassDiagramService(configurationManager);
        this.sequenceDiagramService = new SequenceDiagramService(configurationManager);
        this.storageService = new StorageService(configurationManager);
    }

    /**
     * Coordinates the generation of UML diagrams (class and sequence diagrams).
     * 
     * Responsibilities:
     * - Validates the input directory to ensure it contains `.java` files.
     * - Analyzes the project to extract parsed data.
     * - Generates class diagrams if specified in the configuration.
     * - Generates sequence diagrams if specified in the configuration.
     * - Stores the generated diagrams in the specified storage location.
     * 
     * Preconditions:
     * - The input directory must exist and contain valid `.java` files.
     * - The configuration must specify the types of diagrams to generate.
     * 
     * Postconditions:
     * - The specified UML diagrams are generated and stored successfully.
     * 
     * Usage Example:
     * {@code
     * UMLDiagramGenerator generator = new UMLDiagramGenerator(config);
     * generator.generateDiagrams();
     * }
     * 
     * @throws RuntimeException if diagram generation fails due to invalid input or configuration.
     * @since 1.0
     */
    public void generateDiagrams() {
        validateInputDirectory(configurationManager.getProperty("input.directory"));
        try {
            // Analyze the project and extract parsed data
            List<CodeEntity> parsedData = projectAnalyzer.analyzeProject();

            // Get the types of diagrams to generate from the configuration
            String diagramTypes = configurationManager.getProperty("diagram.types");
            if (diagramTypes == null || diagramTypes.isEmpty()) {
                throw new IllegalArgumentException("No diagram types specified in the configuration.");
            }

            // Generate class diagrams if requested
            if (diagramTypes.contains("class")) {
                logger.info("Generating class diagram...");
                String classDiagramPath = classDiagramService.generateClassDiagram(parsedData);
                storageService.storeDiagram(classDiagramPath, "class");
            }

            // Generate sequence diagrams if requested
            if (diagramTypes.contains("sequence")) {
                logger.info("Generating sequence diagram...");
                String sequenceDiagramPath = sequenceDiagramService.generateSequenceDiagram(parsedData);
                storageService.storeDiagram(sequenceDiagramPath, "sequence");
            }




            logger.info("UML diagrams generated successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate UML diagrams: " + e.getMessage(), e);
        }
    }

    /**
     * Validates the input directory to ensure it exists and contains `.java` files.
     * 
     * Responsibilities:
     * - Checks if the input directory exists and is a valid directory.
     * - Recursively searches for `.java` files in the directory and its subdirectories.
     * 
     * Preconditions:
     * - The input directory path must not be null or empty.
     * 
     * Postconditions:
     * - The input directory is validated, and `.java` files are found.
     * 
     * @param inputDir the path to the input directory.
     * @throws IllegalArgumentException if the directory is invalid or contains no `.java` files.
     * @since 1.0
     */
    private void validateInputDirectory(String inputDir) {
        File dir = new File(inputDir);

        // Check if the directory exists and is a directory
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("The input directory does not exist or is not a directory: " + inputDir);
        }

        // Recursively search for .java files
        List<File> javaFiles = findJavaFiles(dir);
        if (javaFiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "No input files found in the input directory or its subdirectories: " + inputDir);
        }

        logger.info("Input directory validated successfully. Found {} .java files.", javaFiles.size());
    }

    /**
     * Recursively searches for `.java` files in the specified directory and its subdirectories.
     * 
     * Responsibilities:
     * - Traverses the directory structure to find `.java` files.
     * 
     * @param dir the directory to search.
     * @return a list of `.java` files found in the directory and its subdirectories.
     * @since 1.0
     */
    private List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively search in subdirectories
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }
}