package com.pjsoft.j2arch.uml.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.config.SymbolSolverConfig;
import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.model.FieldEntity;
import com.pjsoft.j2arch.uml.model.MethodEntity;
import com.pjsoft.j2arch.uml.model.Relative;

/**
 * Service for parsing Java source files and extracting code entities for UML diagram generation.
 * 
 * This class uses JavaParser to parse `.java` files and extract information about classes,
 * methods, fields, and relationships. It supports resolving types and relationships using
 * the JavaSymbolSolver.
 * 
 * Responsibilities:
 * - Parses Java source files to extract code entities.
 * - Resolves relationships such as inheritance, method calls, and field accesses.
 * - Configures the symbol solver for type resolution.
 * - Filters classes based on the "include.package" configuration property.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * JavaParserService parserService = new JavaParserService(config);
 * List<CodeEntity> entities = parserService.parseFiles(filePaths);
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}
 * - {@link SymbolSolverConfig}
 * - JavaParser library
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Limitations:
 * - Assumes that the input files are valid `.java` files.
 * - Requires a valid configuration file with the input directory specified.
 * - Filters classes based on the "include.package" property. If not set, all classes are included.
 * 
 * @author PJSoft
 * @version 1.2
 * @since 1.0
 */
public class JavaParserService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaParserService.class);

    private final ConfigurationManager config;

    /**
     * Constructs a new JavaParserService with the specified configuration manager.
     * 
     * @param config the configuration manager containing project settings.
     * @since 1.0
     */
    public JavaParserService(ConfigurationManager config) {
        this.config = config;
    }

    /**
     * Parses a list of Java source files and extracts {@link CodeEntity} objects.
     * 
     * Responsibilities:
     * - Configures the symbol solver for type resolution.
     * - Parses each file to extract classes, methods, fields, and relationships.
     * - Filters classes based on the "include.package" configuration property.
     * 
     * Preconditions:
     * - The input files must be valid `.java` files.
     * 
     * Postconditions:
     * - A list of {@link CodeEntity} objects is returned, representing parsed classes.
     * 
     * @param files the list of file paths to parse.
     * @return a list of {@link CodeEntity} objects representing parsed classes.
     * @throws IOException if a file cannot be read or parsed.
     * @since 1.0
     */
    public List<CodeEntity> parseFiles(List<String> files) {
        SymbolSolverConfig.configureSymbolSolver(config.getProperty("input.directory")); // Configure SymbolSolver
        List<CodeEntity> parsedEntities = new ArrayList<>();

        for (String filePath : files) {
            try {
                File file = new File(filePath);
                CompilationUnit compilationUnit = StaticJavaParser.parse(file);

                Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit
                        .findFirst(ClassOrInterfaceDeclaration.class);

                if (classDeclaration.isPresent()) {
                    ClassOrInterfaceDeclaration classDecl = classDeclaration.get();

                    // Use fully qualified name if available, otherwise fallback to simple name
                    String fullyQualifiedName = classDecl.getFullyQualifiedName().orElse(classDecl.getNameAsString());

                    // Filter classes based on the "include.package" configuration
                    String includePackage = config.getProperty("include.package");
                    if (includePackage != null && !includePackage.isEmpty() && !fullyQualifiedName.startsWith(includePackage)) {
                        logger.debug("Skipping class outside the include package: {}", fullyQualifiedName);
                        continue; // Skip this class if it does not belong to the specified package
                    }

                    CodeEntity codeEntity = new CodeEntity(fullyQualifiedName);

                    // Extract relationships and details
                    extractParentRelationships(classDecl, codeEntity);
                    extractMethodsAndRelationships(classDecl, codeEntity);
                    extractFieldsAndRelationships(classDecl, codeEntity);

                    // Add the parsed entity
                    parsedEntities.add(codeEntity);

                    // Log relationships for debugging
                    logRelationships(codeEntity);
                }
            } catch (IOException e) {
                logger.error("Error parsing file: {}", filePath, e);
            }
        }
        return parsedEntities;
    }

    /**
     * Extracts parent relationships (e.g., inheritance) from a class declaration.
     * 
     * @param classDecl the class or interface declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @since 1.0
     */
    private void extractParentRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity) {
        classDecl.getExtendedTypes().forEach(parent -> {
            try {
                ResolvedType resolvedType = parent.resolve();
                if (resolvedType.isReferenceType()) {
                    ResolvedReferenceType referenceType = resolvedType.asReferenceType();
                    String parentName = referenceType.getQualifiedName(); // Fully qualified name

                    if (isIrrelevantEntity(parentName)) {
                        logger.debug("Skipping irrelevant parent: {}", parentName);
                        return;
                    }

                    Relative parentRelative = new Relative(Relative.RelationshipType.INHERITANCE,
                            new CodeEntity(parentName));
                    codeEntity.addRelative(parentRelative);
                }
            } catch (Exception e) {
                logger.warn("Failed to resolve parent type: {}", parent, e);
            }
        });
    }

    /**
     * Extracts methods and their relationships (e.g., method calls) from a class declaration.
     * 
     * @param classDecl the class or interface declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @since 1.0
     */
    private void extractMethodsAndRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity) {
        for (MethodDeclaration method : classDecl.getMethods()) {
            String visibility = method.getAccessSpecifier().asString();
            MethodEntity methodEntity = new MethodEntity(method.getNameAsString(), method.getTypeAsString());
            methodEntity.setVisibility(visibility);
            codeEntity.addMethod(methodEntity);

            method.findAll(MethodCallExpr.class).forEach(call -> {
                String calleeClassName = resolveCalleeClassName(call, codeEntity);
                if (calleeClassName == null || isIrrelevantEntity(calleeClassName)) {
                    return;
                }
                String calleeMethodName = call.getNameAsString();
                Relative calleeRelative = new Relative(Relative.RelationshipType.CALLER_CALLEE,
                        new CodeEntity(calleeClassName), calleeMethodName, method.getNameAsString());
                codeEntity.addRelative(calleeRelative);
            });

            // Extract field access relationships
            extractFieldAccessRelationships(method, codeEntity);
        }
    }

    /**
     * Extracts fields and their relationships (e.g., associations) from a class declaration.
     * 
     * @param classDecl the class or interface declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @since 1.0
     */
    private void extractFieldsAndRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity) {
        classDecl.getFields().forEach(field -> {
            String visibility = field.getAccessSpecifier().asString();
            field.getVariables().forEach(variable -> {
                FieldEntity fieldEntity = new FieldEntity(variable.getNameAsString(), variable.getType().asString());
                fieldEntity.setVisibility(visibility);
                codeEntity.addField(fieldEntity);

                String fieldType = variable.getType().asString();
                // Skip self-referencing fields
                if (fieldType.equals(codeEntity.getName())) {
                    logger.debug("Skipping self-referencing field in class: {}", codeEntity.getName());
                    return;
                }

                if (isProjectEntity(fieldType)) {
                    Relative associationRelative = new Relative(Relative.RelationshipType.ASSOCIATION,
                            new CodeEntity(fieldType));
                    codeEntity.addRelative(associationRelative);
                } else {
                    logger.debug("Skipping association for non-project entity: {}", fieldType);
                }
            });
        });
    }

    /**
     * Extracts field access relationships from a method declaration.
     * 
     * @param method the method declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @since 1.0
     */
    private void extractFieldAccessRelationships(MethodDeclaration method, CodeEntity codeEntity) {
        method.findAll(FieldAccessExpr.class).forEach(fieldAccess -> {
            // Resolve the scope of the field access
            String accessedClassName = Optional.ofNullable(fieldAccess.getScope())
                    .map(scope -> {
                        try {
                            ResolvedType resolvedType = scope.calculateResolvedType();
                            if (resolvedType.isReferenceType()) {
                                ResolvedReferenceType referenceType = resolvedType.asReferenceType();
                                return referenceType.getQualifiedName(); // Fully qualified name
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to resolve field access scope: {}", scope, e);
                        }
                        return "Unknown";
                    })
                    .orElse("Unknown");

            // Skip self-referencing field accesses
            if (accessedClassName.equals(codeEntity.getName())) {
                logger.debug("Skipping self-referencing field access in class: {}", codeEntity.getName());
                return;
            }

            // Check if the accessed class is relevant
            if (isIrrelevantEntity(accessedClassName)) {
                logger.debug("Skipping irrelevant field access: {}", accessedClassName);
                return;
            }

            // Add an association relationship for the accessed variable
            Relative fieldAccessRelative = new Relative(
                    Relative.RelationshipType.ASSOCIATION,
                    new CodeEntity(accessedClassName),
                    fieldAccess.getNameAsString(), // Field name
                    method.getNameAsString()); // Accessing method
            codeEntity.addRelative(fieldAccessRelative);

            logger.debug("Added association: Class {} -> Field {} of Class {}",
                    codeEntity.getName(), fieldAccess.getNameAsString(), accessedClassName);
        });
    }

    /**
     * Resolves the class name of a method call expression.
     * 
     * @param call the method call expression.
     * @param codeEntity the {@link CodeEntity} representing the calling class.
     * @return the fully qualified name of the callee class, or {@code null} if it cannot be resolved.
     * @since 1.0
     */
    private String resolveCalleeClassName(MethodCallExpr call, CodeEntity codeEntity) {
        return call.getScope().map(scope -> {
            try {
                ResolvedType resolvedType = scope.calculateResolvedType();
                if (resolvedType.isReferenceType()) {
                    ResolvedReferenceType referenceType = resolvedType.asReferenceType();
                    return referenceType.getQualifiedName(); // Fully qualified name
                }
            } catch (Exception e) {
                logger.warn("Failed to resolve type for scope: {}", scope, e);
            }
            return "Unknown";
        }).orElse(codeEntity.getName());
    }

    /**
     * Logs the relationships of a code entity for debugging purposes.
     * 
     * @param codeEntity the {@link CodeEntity} whose relationships are to be logged.
     * @since 1.0
     */
    private void logRelationships(CodeEntity codeEntity) {
        List<Relative> relatives = codeEntity.getRelatives();
        for (Relative relative : relatives) {
            logger.debug("Relative Type: {}, Target Entity: {}", relative.getRelationshipType(),
                    relative.getCalleeEntity().getName());
        }
    }

    /**
     * Determines if an entity is irrelevant for UML diagram generation.
     * 
     * @param entityName the name of the entity.
     * @return {@code true} if the entity is irrelevant, {@code false} otherwise.
     * @since 1.0
     */
    private boolean isIrrelevantEntity(String entityName) {
        return isLibraryEntity(entityName) || !isProjectEntity(entityName);
    }

    /**
     * Determines if an entity belongs to a library.
     * 
     * @param entityName the name of the entity.
     * @return {@code true} if the entity belongs to a library, {@code false} otherwise.
     * @since 1.0
     */
    private boolean isLibraryEntity(String entityName) {
        return entityName.startsWith("java.") || entityName.startsWith("javax.")
                || entityName.startsWith("org.springframework.");
    }

    /**
     * Determines if an entity belongs to the project.
     * 
     * Responsibilities:
     * - Checks if the entity name starts with the package specified in "include.package".
     * - If "include.package" is not set, all entities are considered part of the project.
     * 
     * @param entityName the name of the entity.
     * @return {@code true} if the entity belongs to the project, {@code false} otherwise.
     * @since 1.0
     */
    private boolean isProjectEntity(String entityName) {
        String projectPackage = config.getProperty("include.package");
        if (projectPackage == null || projectPackage.isEmpty()) {
            // If "include.package" is not set, include all entities
            return true;
        }
        return entityName.startsWith(projectPackage);
    }
}