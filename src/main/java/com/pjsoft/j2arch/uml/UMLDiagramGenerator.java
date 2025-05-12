package com.pjsoft.j2arch.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
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
 * - Analyzes the project to extract parsed data (e.g., classes, methods,
 * relationships).
 * - Generates class and sequence diagrams based on the configuration provided
 * in the context.
 * - Tracks progress using the ProgressTracker.
 * - Logs the status and results of the diagram generation process.
 * 
 * Dependencies:
 * - {@link UMLGenerationContext}: Provides configuration details for UML
 * generation (e.g., input directory, diagram types).
 * - {@link ProjectAnalyzer}: Analyzes the project to extract code entities and
 * relationships.
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
     * @param context         The UML generation context containing configuration
     *                        details.
     * @param progressTracker The progress tracker to monitor and update the
     *                        progress of the generation process.
     * 
     * @throws RuntimeException if any error occurs during the diagram generation
     *                          process.
     */
    public void generateDiagrams(UMLGenerationContext context, ProgressTracker progressTracker) {
        try {
            // Start project analysis
            progressTracker.onStatusUpdate("Starting project analysis....");

            // Analyze the project and extract parsed data
            List<CodeEntity> parsedData = projectAnalyzer.analyzeProject(context, progressTracker);
            Map<String, PackageEntity> packageEntities = projectAnalyzer.analyzeProjectForPackages(context,
                    progressTracker);
            progressTracker.onStatusUpdate("Project analysis completed.");

            // Get the types of diagrams to generate from the context
            List<String> diagramTypes = List.of(context.getDiagramTypes().split(","));

            // Generate diagrams based on the specified types
            for (String diagramType : diagramTypes) {
                switch (diagramType.toLowerCase()) {
                    case "class":
                        logger.debug("Generating class diagram...");
                        progressTracker.onStatusUpdate("Class diagram generation started ....");
                        // String classDiagramPath =
                        // classDiagramService.generateUnifiedClassDiagram(parsedData, context);
                        classDiagramService.generateClassDiagramByPackage(packageEntities, context, progressTracker);
                        progressTracker.onStatusUpdate("Class diagram generated.");
                        // progressTracker.addCompletedUnits(WorkUnitType.CLASS_DIAGRAM, 1);
                        break;

                    case "sequence":
                        logger.debug("Generating sequence diagram...");
                        progressTracker.onStatusUpdate("Sequence diagram generation started ....");
                        String sequenceDiagramPath = sequenceDiagramService.generateSequenceDiagram(parsedData, context,
                                progressTracker);

                        progressTracker.onStatusUpdate("Sequence diagram generated.");
                        // progressTracker.addCompletedUnits(WorkUnitType.SEQUENCE_DIAGRAM, 1);
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

   

}