package com.pjsoft.j2arch.uml.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.FieldEntity;
import com.pjsoft.j2arch.core.model.MethodEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.core.model.Relative;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.WorkUnitType;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.uml.util.DiagramImageGenerator;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * Service for generating class diagrams using PlantUML.
 * 
 * This class provides functionality to generate class diagrams from a list of
 * {@link CodeEntity} objects. It creates a `.puml` file with the class
 * definitions
 * and relationships, and then generates a diagram image using PlantUML.
 * 
 * Responsibilities:
 * - Generates a unified `.puml` file containing class definitions and
 * relationships.
 * - Uses PlantUML to generate diagram images from the `.puml` file.
 * - Filters classes based on the "include.package" configuration property.
 * - Ensures output directories and files are created and managed properly.
 * - Supports generating individual class diagrams for specific entities.
 * 
 * Dependencies:
 * - {@link ConfigurationManager}: Provides configuration details for the
 * project.
 * - {@link CodeEntity}: Represents a class or interface in the codebase.
 * - {@link DiagramImageGenerator}: Handles the generation of diagram images
 * from `.puml` files.
 * - PlantUML library: Used for generating UML diagrams.
 * 
 * Limitations:
 * - Assumes that the provided {@link CodeEntity} objects are valid and
 * complete.
 * - Requires PlantUML to be properly configured in the environment.
 * - Filters classes based on the "include.package" property. If not set, all
 * classes are included.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * ClassDiagramService service = new ClassDiagramService(config);
 * List<CodeEntity> codeEntities = ...; // Extracted from the project
 * String diagramPath = service.generateUnifiedClassDiagram(codeEntities, context);
 * System.out.println("Class diagram generated at: " + diagramPath);
 * }
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public class ClassDiagramService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClassDiagramService.class);

    /**
     * Constructs a new ClassDiagramService.
     * 
     * Responsibilities:
     * - Initializes the service for class diagram generation.
     */
    public ClassDiagramService() {
        // No explicit initialization required for now
    }

    /**
     * Generates a unified class diagram from the provided list of
     * {@link CodeEntity} objects.
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
     * @param codeEntities the list of {@link CodeEntity} objects to include in the
     *                     diagram.
     * @param context      the UML generation context containing configuration
     *                     details.
     * @return the path to the generated class diagram image.
     * @throws RuntimeException if the diagram generation fails.
     */
    public String generateUnifiedClassDiagram(List<CodeEntity> codeEntities, UMLGenerationContext context) {
        logger.debug("Starting unified class diagram generation.");

        // Generate the unified .puml file
        generateUnifiedPumlFile(codeEntities, context);

        // Generate the diagram image from the .puml file
        File pumlFile = new File(PathResolver.resolvePath(context.getPumlPath(), context.getUnifiedClassDiagram()));
        File imageDir = new File(context.getImagesOutputDirectory());
        DiagramImageGenerator imageGenerator = new DiagramImageGenerator();
        imageGenerator.generateDiagramImage(pumlFile, imageDir);

        logger.debug("Unified class diagram generation completed.");

        return context.getImagesOutputDirectory() + context.getUnifiedClassDiagram();
    }

    /**
     * Generates a unified `.puml` file containing class definitions and
     * relationships.
     * 
     * Responsibilities:
     * - Writes class definitions, fields, methods, and relationships to the `.puml`
     * file.
     * - Filters classes based on the "include.package" configuration property.
     * 
     * Preconditions:
     * - The list of {@link CodeEntity} objects must not be null or empty.
     * 
     * Postconditions:
     * - A `.puml` file is created with the class definitions and relationships.
     * 
     * @param codeEntities the list of {@link CodeEntity} objects to include in the
     *                     `.puml` file.
     * @param context      the UML generation context containing configuration
     *                     details.
     * @throws RuntimeException if the `.puml` file cannot be created or written to.
     */
    private void generateUnifiedPumlFile(List<CodeEntity> codeEntities, UMLGenerationContext context) {
        if (codeEntities.isEmpty()) {
            throw new IllegalArgumentException("No code entities provided for generating the class diagram.");
        }

        // Filter classes based on the "include.package" configuration
        String includePackage = context.getIncludePackage();
        if (includePackage != null && !includePackage.isEmpty()) {
            codeEntities = codeEntities.stream()
                    .filter(entity -> entity.getName().startsWith(includePackage))
                    .collect(Collectors.toList());
        }

        String pumlFileName = context.getPumlPath() + File.separator + context.getUnifiedClassDiagram();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFileName))) {
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
            logger.debug("Unified PUML file generated: " + pumlFileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate unified PUML file: " + pumlFileName, e);
        }
    }

    /**
     * Generates an individual class diagram for a specific {@link CodeEntity}.
     * 
     * Responsibilities:
     * - Creates a `.puml` file for the given class.
     * - Generates a diagram image from the `.puml` file using PlantUML.
     * - Updates the {@link CodeEntity} with the path to the generated diagram.
     * 
     * @param codeEntity the {@link CodeEntity} for which the diagram is to be
     *                   generated.
     * @param context    the JavaDoc generation context containing configuration
     *                   details.
     */
    public void generateClassDiagram(CodeEntity codeEntity, JavaDocGenerationContext context) {
        // Step 1: Generate the `.puml` file for the individual entity
        String pumlFileName = context.getPumlPath() + File.separator + codeEntity.getName().replace(".", "_") + ".puml";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFileName))) {
            writer.write("@startuml\n");
            writer.write("skinparam linetype Ortho\n");

            // Delegate writing the actual class diagram content
            writeClassDiagram(codeEntity, writer);

            writer.write("@enduml\n");
            logger.debug("PUML file generated for class: {}", codeEntity.getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PUML file for class: " + codeEntity.getName(), e);
        }

        // Step 2: Generate the image using the `.puml` file
        File pumlFile = new File(pumlFileName);
        File imageDir = new File(context.getImagesOutputDirectory());
        // System.out.println("Class Diagram Service Generate class diagram, puml file
        // and image dir: "+pumlFileName+ " "+imageDir);
        DiagramImageGenerator imageGenerator = new DiagramImageGenerator();
        imageGenerator.generateDiagramImage(pumlFile, imageDir);
        logger.debug("Class diagram image generated for class: {}", codeEntity.getName());

        // Step 3: Set the diagram path in the code entity
        String imageFilePath = codeEntity.getName().replace(".", "_") + ".png";
        codeEntity.setClassDiagram(imageFilePath);
        logger.debug("Class diagram generation completed for class: {}", codeEntity.getName());
    }

    public void generateClassDiagramByPackage(Map<String, PackageEntity> packageEntities, UMLGenerationContext context,
            ProgressTracker progressTracker) {
        Collection<PackageEntity> packages = packageEntities.values();
                progressTracker.addTotalUnits(WorkUnitType.CLASS_DIAGRAM, packages.size());
        // Iterate over all packages to generate class diagrams for each package
        for (PackageEntity packageEntity : packages) {
            // Generate the start of the UML diagram for the package
            String pumlFileName = context.getPumlPath() + File.separator + packageEntity.getName().replace(".", "_")
                    + "_package.puml";
            File pumlFile = new File(pumlFileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFile))) {
                writer.write("@startuml\n");
                writer.write("skinparam linetype Ortho\n");

                // Write package name
                writer.write("package " + simpleName(packageEntity.getName()) + " {\n");

                // Generate class diagrams for each class in the package using the common method
                for (CodeEntity codeEntity : packageEntity.getClasses()) {
                    writeClassDiagram(codeEntity, writer);
                }

                // Close package block
                writer.write("}\n");
                writer.write("@enduml\n");
                logger.debug("PUML file generated for package: " + packageEntity.getName());

                

            } catch (IOException e) {
                throw new RuntimeException("Failed to generate PUML file for package: " + packageEntity.getName(), e);
            }

            // Step 2: Generate the image using the `.puml` file
                
            try {
                File imageDir = new File(context.getImagesOutputDirectory());

                DiagramImageGenerator imageGenerator = new DiagramImageGenerator();

                imageGenerator.generateDiagramImage(pumlFile, imageDir);
            } catch (Exception e) {
                logger.error("Failed to generate image for package: {}", packageEntity.getName(), e);
            }

                // Step 3: Set the diagram path in the code entity
                // String imageFilePath = packageEntity.getName().replace(".", "_") + ".png";
                // packageEntity.setPackageDiagram(imageFilePath);

                // Log the package diagram creation

            progressTracker.addCompletedUnits(WorkUnitType.CLASS_DIAGRAM, 1);
           
            
        }
    }

    // Currently not in use method
    public void generateClassDiagram(CodeEntity codeEntity, UMLGenerationContext context) {
        // Generate PUML for a single class
        String pumlFileName = context.getPumlPath() + File.separator + codeEntity.getName().replace(".", "_") + ".puml";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFileName))) {
            writer.write("@startuml\n");
            writer.write("skinparam linetype Ortho\n");

            // Write the class diagram for this specific class
            writeClassDiagram(codeEntity, writer);

            writer.write("@enduml\n");
            logger.debug("PUML file generated for class: " + codeEntity.getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PUML file for class: " + codeEntity.getName(), e);
        }
    }

    private void writeClassDiagram(CodeEntity codeEntity, BufferedWriter writer) {
        try {
            // Add class definition
            writer.write("class " + simpleName(codeEntity.getName()) + " {\n");

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

            // Add relationships
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
                        relationship = simpleName(sourceClassName) + " <|-- " + simpleName(targetClassName)
                                + " : extends";
                        break;
                    case IMPLEMENTATION:
                        relationship = simpleName(sourceClassName) + " <|.. " + simpleName(targetClassName)
                                + " : implements";
                        break;
                    case ASSOCIATION:
                        relationship = simpleName(sourceClassName) + " -- " + simpleName(targetClassName)
                                + " : association";
                        break;
                    case CALLER_CALLEE:
                        relationship = simpleName(sourceClassName) + " --> " + simpleName(targetClassName)
                                + " : caller-callee";
                        break;
                    default:
                        relationship = ""; // Unsupported relationship type
                }

                if (!relationship.isEmpty()) {
                    writer.write(relationship + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write class diagram for: " + codeEntity.getName(), e);
        }
    }

    /**
     * Extracts the simple name of a class from its fully qualified name.
     * 
     * @param fullyQualifiedName the fully qualified name of the class.
     * @return the simple name of the class.
     */
    private static String simpleName(String fullyQualifiedName) {
        return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1);
    }

}