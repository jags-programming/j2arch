package com.pjsoft.j2arch.uml.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.model.FieldEntity;
import com.pjsoft.j2arch.uml.model.MethodEntity;
import com.pjsoft.j2arch.uml.model.Relative;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * Service for generating class diagrams using PlantUML.
 * 
 * This class provides functionality to generate class diagrams from a list of {@link CodeEntity}
 * objects. It creates a `.puml` file with the class definitions and relationships, and then
 * generates a diagram image using PlantUML.
 * 
 * Responsibilities:
 * - Generates a unified `.puml` file containing class definitions and relationships.
 * - Uses PlantUML to generate diagram images from the `.puml` file.
 * - Filters classes based on the "include.package" configuration property.
 * - Ensures output directories and files are created and managed properly.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * ClassDiagramService service = new ClassDiagramService(config);
 * List<CodeEntity> codeEntities = ...; // Extracted from the project
 * String diagramPath = service.generateClassDiagram(codeEntities);
 * System.out.println("Class diagram generated at: " + diagramPath);
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}
 * - {@link CodeEntity}
 * - PlantUML library
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Limitations:
 * - Assumes that the provided {@link CodeEntity} objects are valid and complete.
 * - Requires PlantUML to be properly configured in the environment.
 * - Filters classes based on the "include.package" property. If not set, all classes are included.
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public class ClassDiagramService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClassDiagramService.class);
    private final ConfigurationManager config;

    /**
     * Constructs a new ClassDiagramService with the specified configuration manager.
     * 
     * @param config the configuration manager containing project settings.
     * @since 1.0
     */
    public ClassDiagramService(ConfigurationManager config) {
        this.config = config;
    }

    /**
     * Generates a class diagram from the provided list of {@link CodeEntity} objects.
     * 
     * Responsibilities:
     * - Creates a `.puml` file with class definitions and relationships.
     * - Generates a diagram image from the `.puml` file using PlantUML.
     * - Filters classes based on the "include.package" configuration property.
     * 
     * Preconditions:
     * - The list of {@link CodeEntity} objects must not be null or empty.
     * 
     * Postconditions:
     * - A class diagram image is generated and saved to the output directory.
     * 
     * @param codeEntities the list of {@link CodeEntity} objects to include in the diagram.
     * @return the path to the generated class diagram image.
     * @throws RuntimeException if the diagram generation fails.
     * @since 1.0
     */
    public String generateClassDiagram(List<CodeEntity> codeEntities) {
        logger.info("Going to generate class diagrams.");
        String outputDirectory = config.getProperty("output.directory");
        String pumlFilePath = outputDirectory + "/classDiagram.puml";

        // Ensure the output directory exists
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDirectory);
        }

        // Generate the unified .puml file
        generateUnifiedPumlFile(codeEntities, pumlFilePath);

        // Generate the diagram image from the .puml file
        generateDiagramImage(pumlFilePath);

        String classDiagramPath = outputDirectory + "/classDiagram.png";
        logger.info("Class diagram generated at: " + classDiagramPath);

        return classDiagramPath;
    }

    /**
     * Generates a unified `.puml` file containing class definitions and relationships.
     * 
     * Responsibilities:
     * - Writes class definitions, fields, methods, and relationships to the `.puml` file.
     * - Filters classes based on the "include.package" configuration property.
     * 
     * Preconditions:
     * - The list of {@link CodeEntity} objects must not be null or empty.
     * 
     * Postconditions:
     * - A `.puml` file is created with the class definitions and relationships.
     * 
     * @param codeEntities the list of {@link CodeEntity} objects to include in the `.puml` file.
     * @param pumlFilePath the path to the `.puml` file to be created.
     * @throws RuntimeException if the `.puml` file cannot be created or written to.
     * @since 1.0
     */
    private void generateUnifiedPumlFile(List<CodeEntity> codeEntities, String pumlFilePath) {
        if (codeEntities.isEmpty()) {
            throw new IllegalArgumentException("No code entities provided for generating the class diagram.");
        }

        // Filter classes based on the "include.package" configuration
        String includePackage = config.getProperty("include.package");
        if (includePackage != null && !includePackage.isEmpty()) {
            codeEntities = codeEntities.stream()
                .filter(entity -> entity.getName().startsWith(includePackage))
                .collect(Collectors.toList());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFilePath))) {
            writer.write("@startuml\n");
            writer.write("skinparam linetype Ortho\n");

            // Add each class to the .puml file
            for (CodeEntity codeEntity : codeEntities) {
                writer.write("class " + codeEntity.getName() + " {\n");

                // Add fields
                for (FieldEntity field : codeEntity.getFields()) {
                    writer.write("    " + field.getType() + " " + field.getName() + "\n");
                }

                // Add methods
                for (MethodEntity method : codeEntity.getMethods()) {
                    StringBuilder methodSignature = new StringBuilder();

                    // Add visibility (if available)
                    if (method.getVisibility() != null) {
                        methodSignature.append(method.getVisibility()).append(" ");
                    }

                    // Add return type and method name
                    methodSignature.append(method.getReturnType()).append(" ").append(method.getName()).append("(");

                    // Add parameter types
                    methodSignature.append(String.join(", ", method.getParameters()));

                    methodSignature.append(")");

                    writer.write("    " + methodSignature + "\n");
                }

                writer.write("}\n");
            }

            // Add relationships between classes
            Set<String> uniqueRelationships = new HashSet<>();
            for (CodeEntity codeEntity : codeEntities) {
                for (Relative relative : codeEntity.getRelatives()) {
                    String sourceClassName = codeEntity.getName();
                    String targetClassName = relative.getCalleeEntity().getName();

                    // Skip self-association relationships
                    if (sourceClassName.equals(targetClassName)) {
                        logger.debug("Skipping self-association for class: {}", sourceClassName);
                        continue;
                    }

                    // Determine the correct PlantUML notation based on the relationship type
                    String relationship;
                    switch (relative.getRelationshipType()) {
                        case INHERITANCE:
                            relationship = sourceClassName + " <|-- " + targetClassName + " : extends";
                            break;
                        case IMPLEMENTATION:
                            relationship = sourceClassName + " <|.. " + targetClassName + " : implements";
                            break;
                        case ASSOCIATION:
                            relationship = sourceClassName + " -- " + targetClassName + " : association";
                            break;
                        case CALLER_CALLEE:
                            relationship = sourceClassName + " --> " + targetClassName + " : caller-callee";
                            break;
                        default:
                            relationship = ""; // Unsupported relationship type
                    }

                    if (!relationship.isEmpty() && uniqueRelationships.add(relationship)) {
                        writer.write(relationship + "\n");
                    }
                }
            }

            writer.write("@enduml\n");
            logger.info("Unified PUML file generated: " + pumlFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate unified PUML file: " + pumlFilePath, e);
        }
    }

    /**
     * Generates a diagram image from the specified `.puml` file using PlantUML.
     * 
     * Responsibilities:
     * - Reads the `.puml` file and generates a diagram image.
     * 
     * Preconditions:
     * - The `.puml` file must exist and be readable.
     * 
     * Postconditions:
     * - A diagram image is generated and saved to the output directory.
     * 
     * @param pumlFilePath the path to the `.puml` file.
     * @throws RuntimeException if the diagram image cannot be generated.
     * @since 1.0
     */
    private void generateDiagramImage(String pumlFilePath) {
        try {
            File pumlFile = new File(pumlFilePath);
            if (!pumlFile.exists()) {
                throw new IllegalArgumentException("PUML file not found at: " + pumlFilePath);
            }

            // Use PlantUML's SourceFileReader to generate the diagram
            SourceFileReader reader = new SourceFileReader(pumlFile);
            List<GeneratedImage> generatedImages = reader.getGeneratedImages();

            if (generatedImages.isEmpty()) {
                throw new RuntimeException("No diagram images were generated for: " + pumlFilePath);
            }

            for (GeneratedImage image : generatedImages) {
                logger.info("Generated diagram image: " + image.getPngFile().getAbsolutePath());
            }

            logger.info("Diagram image generated successfully for: " + pumlFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Error generating diagram image for: " + pumlFilePath, e);
        }
    }
}
