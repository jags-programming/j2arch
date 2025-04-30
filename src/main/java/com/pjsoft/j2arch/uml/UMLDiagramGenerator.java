package com.pjsoft.j2arch.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.WorkUnitType;
import com.pjsoft.j2arch.uml.service.ClassDiagramService;
import com.pjsoft.j2arch.uml.service.SequenceDiagramService;
import com.pjsoft.j2arch.uml.service.StorageService;
import com.pjsoft.j2arch.uml.util.ProjectAnalyzer;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * Coordinates the generation of UML diagrams (class and sequence diagrams).
 * 
 * Responsibilities:
 * - Validates the input directory and ensures it contains valid `.java` files.
 * - Analyzes the project to extract parsed data (e.g., classes, methods, relationships).
 * - Generates class and sequence diagrams based on the configuration provided in the context.
 * - Tracks progress using the ProgressTracker.
 * - Logs the status and results of the diagram generation process.
 * 
 * Dependencies:
 * - {@link UMLGenerationContext}: Provides configuration details for UML generation (e.g., input directory, diagram types).
 * - {@link ProjectAnalyzer}: Analyzes the project to extract code entities and relationships.
 * - {@link ClassDiagramService}: Generates class diagrams.
 * - {@link SequenceDiagramService}: Generates sequence diagrams.
 * - {@link StorageService}: Handles storage of generated diagrams.
 * 
 * Limitations:
 * - Currently supports only "class" and "sequence" diagram types.
 * - Assumes that input validation is performed at the GUI level.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class UMLDiagramGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UMLDiagramGenerator.class);

    private final ProjectAnalyzer projectAnalyzer;
    private final ClassDiagramService classDiagramService;
    private final SequenceDiagramService sequenceDiagramService;
    private final StorageService storageService;

    /**
     * Default constructor.
     * Initializes the required services for UML diagram generation.
     */
    public UMLDiagramGenerator() {
        this.projectAnalyzer = new ProjectAnalyzer();
        this.classDiagramService = new ClassDiagramService();
        this.sequenceDiagramService = new SequenceDiagramService();
        this.storageService = new StorageService();
    }

    /**
     * Generates UML diagrams based on the configuration provided in the context.
     * 
     * Responsibilities:
     * - Analyzes the project to extract code entities.
     * - Generates diagrams (class or sequence) based on the specified types.
     * - Updates the progress tracker during each step of the process.
     * 
     * @param context         The UML generation context containing configuration details.
     * @param progressTracker The progress tracker to monitor and update the progress of the generation process.
     * 
     * @throws RuntimeException if any error occurs during the diagram generation process.
     */
    public void generateDiagrams(UMLGenerationContext context, ProgressTracker progressTracker) {
        try {
            // Start project analysis
            progressTracker.onStatusUpdate("Starting project analysis....");

            // Analyze the project and extract parsed data
            List<CodeEntity> parsedData = projectAnalyzer.analyzeProject(context, progressTracker);
            progressTracker.onStatusUpdate("Project analysis completed.");

            // Get the types of diagrams to generate from the context
            List<String> diagramTypes = List.of(context.getDiagramTypes().split(","));

            // Generate diagrams based on the specified types
            for (String diagramType : diagramTypes) {
                switch (diagramType.toLowerCase()) {
                    case "class":
                        logger.debug("Generating class diagram...");
                        progressTracker.onStatusUpdate("Class diagram generation started ....");
                        String classDiagramPath = classDiagramService.generateUnifiedClassDiagram(parsedData, context);

                        progressTracker.onStatusUpdate("Class diagram generated.");
                        progressTracker.addCompletedUnits(WorkUnitType.CLASS_DIAGRAM, 1);
                        break;

                    case "sequence":
                        logger.debug("Generating sequence diagram...");
                        progressTracker.onStatusUpdate("Sequence diagram generation started ....");
                        String sequenceDiagramPath = sequenceDiagramService.generateSequenceDiagram(parsedData, context);

                        progressTracker.onStatusUpdate("Sequence diagram generated.");
                        progressTracker.addCompletedUnits(WorkUnitType.SEQUENCE_DIAGRAM, 1);
                        break;

                    default:
                        logger.warn("Unsupported diagram type: {}", diagramType);
                }
            }

            logger.debug("UML diagrams generated successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate UML diagrams: " + e.getMessage());
        }
    }

    /**
     * Validates the input directory to ensure it exists and contains `.java` files.
     * 
     * Responsibilities:
     * - Checks if the input directory exists and is a valid directory.
     * - Ensures that the directory contains at least one `.java` file.
     * 
     * @param inputDir The path to the input directory.
     * 
     * @throws IllegalArgumentException if the directory does not exist, is not a directory, or contains no `.java` files.
     */
    private void validateInputDirectory(String inputDir) {
        File dir = new File(inputDir);

        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("The input directory does not exist or is not a directory: " + inputDir);
        }

        List<File> javaFiles = findJavaFiles(dir);
        if (javaFiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "No input files found in the input directory or its subdirectories: " + inputDir);
        }

        logger.debug("Input directory validated successfully. Found {} .java files.", javaFiles.size());
    }

    /**
     * Recursively finds all `.java` files in the specified directory and its subdirectories.
     * 
     * Responsibilities:
     * - Traverses the directory structure to locate `.java` files.
     * 
     * @param dir The directory to search.
     * @return A list of `.java` files found in the directory and its subdirectories.
     */
    private List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }
}