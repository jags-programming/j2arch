package com.pjsoft.j2arch.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import com.pjsoft.j2arch.core.context.GenerationContext;
import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.FieldEntity;
import com.pjsoft.j2arch.core.model.MethodEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.core.model.Relative;
import com.pjsoft.j2arch.core.util.ProgressTracker.WorkUnitType;

/**
 * JavaParserService
 * 
 * A service class for parsing Java source files and extracting structural and 
 * behavioral metadata to support code analysis or UML diagram generation.
 * 
 * This class utilizes JavaParser with JavaSymbolSolver for deep parsing of 
 * Java source code, extracting class declarations, method calls, inheritance 
 * hierarchies, field access, and annotations. It also supports filtering 
 * classes based on a configured package scope.
 * 
 * Responsibilities:
 * - Parse Java source files to extract class-level metadata.
 * - Extract relationships such as inheritance, method calls, and field associations.
 * - Resolve types and symbols using JavaSymbolSolver.
 * - Filter classes based on the "include.package" configuration property.
 * - Track unresolved symbols during parsing for debugging purposes.
 * 
 * Features:
 * - Supports extracting annotations, constructors, methods, fields, and relationships.
 * - Groups parsed classes into packages for further processing.
 * - Tracks progress using a {@link ProgressTracker}.
 * 
 * Dependencies:
 * - {@link ConfigurationManager}: For accessing configuration properties.
 * - {@link SymbolSolverConfig}: For configuring the JavaSymbolSolver.
 * - JavaParser library: For parsing Java source files.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state and uses 
 *   {@link ThreadLocal} for unresolved symbols.
 * 
 * Limitations:
 * - Assumes that the input files are valid `.java` files.
 * - Requires a valid configuration file with the input directory specified.
 * - Filters classes based on the "include.package" property. If not set, all 
 *   classes are included.
 * - Does not validate the correctness of the extracted relationships.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * JavaParserService parserService = new JavaParserService(config);
 * List<CodeEntity> entities = parserService.parseFiles(filePaths, context, progressTracker);
 * }
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public class JavaParserService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaParserService.class);
    private final ThreadLocal<List<String>> unresolvedSymbols = ThreadLocal.withInitial(ArrayList::new);

    /**
     * Parses a list of Java source files and extracts {@link CodeEntity} objects.
     * 
     * Responsibilities:
     * - Configures the symbol solver for type resolution.
     * - Parses each file to extract classes, methods, fields, and relationships.
     * - Filters classes based on the "include.package" configuration property.
     * - Tracks unresolved symbols encountered during parsing.
     * 
     * Preconditions:
     * - The input files must be valid `.java` files.
     * - The input directory must be correctly set in the {@link GenerationContext}.
     * 
     * Postconditions:
     * - A list of {@link CodeEntity} objects is returned, representing parsed classes.
     * - Unresolved symbols are logged for debugging purposes.
     * - Progress is tracked using the {@link ProgressTracker}.
     * 
     * @param files          the list of file paths to parse.
     * @param context        the {@link GenerationContext} containing configuration and context data.
     * @param progressTracker the {@link ProgressTracker} to track parsing progress.
     * @return a list of {@link CodeEntity} objects representing parsed classes.
     * @throws IOException if a file cannot be read or parsed.
     * @since 1.0
     */
    public List<CodeEntity> parseFiles(List<String> files, GenerationContext context, ProgressTracker progressTracker) {
        String inputDir = context.getInputDirectory();
        SymbolSolverConfig.configureSymbolSolver(inputDir, context); // Configure SymbolSolver
        List<CodeEntity> parsedEntities = new ArrayList<>();
        logger.info("Starting project analysis...");
        logger.info("Total files to parse: {}", files.size());

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
                    String includePackage = context.getIncludePackage();
                    if (includePackage != null && !includePackage.isEmpty()
                            && !fullyQualifiedName.startsWith(includePackage)) {
                        logger.debug("Skipping class outside the include package: {}", fullyQualifiedName);
                        continue; // Skip this class if it does not belong to the specified package
                    }

                    CodeEntity codeEntity = new CodeEntity(fullyQualifiedName);

                    // **Extract Class Annotations**
                    classDecl.getAnnotations().forEach(annotation -> {
                        String annotationName = annotation.getNameAsString();
                        codeEntity.addAnnotation(annotationName); // Add annotation to the CodeEntity
                    });

                    // Extract relationships and details
                    extractParentRelationships(classDecl, codeEntity, context);
                    extractMethodsAndRelationships(classDecl, codeEntity, context);
                    extractFieldsAndRelationships(classDecl, codeEntity, context);

                    // Add the parsed entity
                    parsedEntities.add(codeEntity);

                    // Log relationships for debugging
                    // logRelationships(codeEntity);
                } else {
                    logger.warn("No class or interface declaration found in file: {}", file.getName());
                }
            } catch (IOException e) {
                logger.error("Error parsing file: {}", filePath, e);
            } catch (Exception e) {
            logger.error("Error parsing file: {} - {}", filePath, e.getMessage());
            logger.debug("Stack trace:", e);
        } finally {
            // Update progress tracker after processing each file
            progressTracker.addCompletedUnits(WorkUnitType.FILE_PARSING, 1);
                         
        }
            // Update progress tracker after processing each file
        //progressTracker.addCompletedUnits(WorkUnitType.FILE_PARSING,1);

        }

        // Log unresolved symbols after parsing all files
        logUnresolvedSymbols();

        return parsedEntities;
    }

    /**
     * Extracts parent relationships (e.g., inheritance) from a class declaration.
     * 
     * Responsibilities:
     * - Resolves the parent types (extended classes) of the given class declaration.
     * - Adds an inheritance relationship to the {@link CodeEntity} for each resolved parent.
     * - Skips irrelevant parent types based on the project context.
     * 
     * Preconditions:
     * - The class declaration must not be null.
     * - The {@link CodeEntity} must represent the class being analyzed.
     * - The {@link GenerationContext} must provide the necessary configuration for filtering.
     * 
     * Postconditions:
     * - Inheritance relationships are added to the {@link CodeEntity}.
     * - Irrelevant or unresolved parent types are logged and skipped.
     * 
     * @param classDecl  the class or interface declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @param context    the {@link GenerationContext} containing configuration and context data.
     * @since 1.0
     */
    private void extractParentRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity,
            GenerationContext context) {
        classDecl.getExtendedTypes().forEach(parent -> {
            try {
                ResolvedType resolvedType = parent.resolve();
                if (resolvedType.isReferenceType()) {
                    ResolvedReferenceType referenceType = resolvedType.asReferenceType();
                    String parentName = referenceType.getQualifiedName(); // Fully qualified name

                    if (isIrrelevantEntity(parentName, context)) {
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
     * Extracts methods and their relationships (e.g., method calls and field accesses) 
     * from a class declaration.
     * 
     * Responsibilities:
     * - Extracts constructors and their metadata, including visibility, annotations, and parameters.
     * - Extracts methods and their metadata, including visibility, annotations, return types, and parameters.
     * - Identifies method call relationships and adds caller-callee relationships to the {@link CodeEntity}.
     * - Identifies field access relationships within methods and adds association relationships to the {@link CodeEntity}.
     * 
     * Preconditions:
     * - The class declaration must not be null.
     * - The {@link CodeEntity} must represent the class being analyzed.
     * - The {@link GenerationContext} must provide the necessary configuration for filtering.
     * 
     * Postconditions:
     * - Constructors and methods are added to the {@link CodeEntity}.
     * - Caller-callee relationships are added for method calls.
     * - Association relationships are added for field accesses.
     * - Irrelevant or unresolved entities are logged and skipped.
     * 
     * @param classDecl  the class or interface declaration.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @param context    the {@link GenerationContext} containing configuration and context data.
     * @since 1.0
     */
    private void extractMethodsAndRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity,
            GenerationContext context) {
        // Extract constructors
        classDecl.getConstructors().forEach(constructor -> {
            String visibility = constructor.getAccessSpecifier().asString();
            MethodEntity constructorEntity = new MethodEntity(constructor.getNameAsString(), ""); // No return type for
                                                                                                  // constructors
            constructorEntity.setVisibility(visibility);

            // **Extract Annotations**
            constructor.getAnnotations().forEach(annotation -> {
                String annotationName = annotation.getNameAsString();
                constructorEntity.addAnnotation(annotationName); // Add annotation to the MethodEntity
            });

            // Extract parameters
            List<String> parameters = new ArrayList<>();
            constructor.getParameters().forEach(param -> {
                String paramType = param.getType().asString();
                String paramName = param.getNameAsString();
                parameters.add(paramType + " " + paramName);
            });
            constructorEntity.setParameters(parameters);

            codeEntity.addConstructor(constructorEntity);
        });

        for (MethodDeclaration method : classDecl.getMethods()) {
            String visibility = method.getAccessSpecifier().asString();
            MethodEntity methodEntity = new MethodEntity(method.getNameAsString(), method.getTypeAsString());
            methodEntity.setVisibility(visibility);

            // **Extract Annotations**
            method.getAnnotations().forEach(annotation -> {
                String annotationName = annotation.getNameAsString();
                methodEntity.addAnnotation(annotationName); // Add annotation to the MethodEntity
            });

            // Extract parameters
            List<String> parameters = new ArrayList<>();
            method.getParameters().forEach(param -> {
                String paramType = param.getType().asString();
                String paramName = param.getNameAsString();
                parameters.add(paramType + " " + paramName);
            });
            methodEntity.setParameters(parameters);

            codeEntity.addMethod(methodEntity);

            method.findAll(MethodCallExpr.class).forEach(call -> {
                String calleeClassName = resolveCalleeClassName(call, codeEntity);
                if (calleeClassName == null || isIrrelevantEntity(calleeClassName, context)) {
                    return;
                }
                String calleeMethodName = call.getNameAsString();
                Relative calleeRelative = new Relative(Relative.RelationshipType.CALLER_CALLEE,
                        new CodeEntity(calleeClassName), calleeMethodName, method.getNameAsString());
                codeEntity.addRelative(calleeRelative);
            });

            // Extract field access relationships
            extractFieldAccessRelationships(method, codeEntity, context);
        }
    }

    /**
     * Extracts fields and relationships from a given class declaration and updates
     * the provided CodeEntity with the extracted information.
     *
     * @param classDecl   The class or interface declaration to process.
     * @param codeEntity  The CodeEntity object to populate with field and relationship data.
     * @param context     The GenerationContext containing project-specific metadata.
     *
     * This method processes each field in the class declaration, extracting its name,
     * type, visibility, and annotations. It adds the extracted field information to
     * the provided CodeEntity. Additionally, it identifies relationships between the
     * class and other entities in the project. If a field's type matches another
     * project entity, an association relationship is created and added to the CodeEntity.
     *
     * Self-referencing fields (fields with the same type as the class) are skipped.
     * Fields with types that do not belong to the project are also skipped for
     * relationship processing.
     */
    private void extractFieldsAndRelationships(ClassOrInterfaceDeclaration classDecl, CodeEntity codeEntity,
            GenerationContext context) {
        classDecl.getFields().forEach(field -> {
            String visibility = field.getAccessSpecifier().asString();
            field.getVariables().forEach(variable -> {
                FieldEntity fieldEntity = new FieldEntity(variable.getNameAsString(), variable.getType().asString());
                fieldEntity.setVisibility(visibility);

                field.getAnnotations().forEach(annotation -> {
                    String annotationName = annotation.getNameAsString();
                    fieldEntity.addAnnotation(annotationName);
                });

                codeEntity.addField(fieldEntity);

                String fieldType = variable.getType().asString();
                // Skip self-referencing fields
                if (fieldType.equals(codeEntity.getName())) {
                    logger.debug("Skipping self-referencing field in class: {}", codeEntity.getName());
                    return;
                }

                if (isProjectEntity(fieldType, context)) {
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
     * Responsibilities:
     * - Identifies field accesses within the method body.
     * - Resolves the class name of the accessed field's scope.
     * - Adds association relationships to the {@link CodeEntity} for relevant field accesses.
     * - Skips self-referencing field accesses and irrelevant entities.
     * 
     * Preconditions:
     * - The method declaration must not be null.
     * - The {@link CodeEntity} must represent the class being analyzed.
     * - The {@link GenerationContext} must provide the necessary configuration for filtering.
     * 
     * Postconditions:
     * - Association relationships are added to the {@link CodeEntity} for each relevant field access.
     * - Irrelevant or unresolved field accesses are logged and skipped.
     * 
     * @param method     the method declaration to analyze.
     * @param codeEntity the {@link CodeEntity} representing the class.
     * @param context    the {@link GenerationContext} containing configuration and context data.
     * @since 1.0
     */
    private void extractFieldAccessRelationships(MethodDeclaration method, CodeEntity codeEntity,
            GenerationContext context) {
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
            if (isIrrelevantEntity(accessedClassName, context)) {
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
     * Responsibilities:
     * - Resolves the type of the scope of the method call expression.
     * - Returns the fully qualified name of the resolved type if it is a reference type.
     * - Handles unresolved symbols by logging and adding them to the unresolved symbols list.
     * - Returns "Unknown" if the type cannot be resolved.
     * - Defaults to the name of the calling class if the scope is not present.
     * 
     * Preconditions:
     * - The method call expression must not be null.
     * - The {@link CodeEntity} must represent the calling class.
     * 
     * Postconditions:
     * - The fully qualified name of the callee class is returned, or "Unknown" if it cannot be resolved.
     * - Unresolved symbols are logged and added to the unresolved symbols list.
     * 
     * @param call       the method call expression to analyze.
     * @param codeEntity the {@link CodeEntity} representing the calling class.
     * @return the fully qualified name of the callee class, or "Unknown" if it cannot be resolved.
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
            } catch (com.github.javaparser.resolution.UnsolvedSymbolException e) {
                logger.debug("Unresolved symbol for scope: {}", scope, e);
                addUnresolvedSymbol(scope.toString());
            } catch (Exception e) {
                logger.warn("Failed to resolve type for scope: {}", scope, e);
            }
            return "Unknown";
        }).orElse(codeEntity.getName());
    }

    /**
     * Logs the relationships of a code entity for debugging purposes.
     * 
     * @param codeEntity the {@link CodeEntity} whose relationships are to be
     *                   logged.
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
     * Responsibilities:
     * - Checks if the entity belongs to a library or does not belong to the project.
     * - Combines checks for library entities and non-project entities.
     * 
     * Preconditions:
     * - The entity name must not be null.
     * - The {@link GenerationContext} must provide the necessary configuration for filtering.
     * 
     * Postconditions:
     * - Returns {@code true} if the entity is irrelevant, {@code false} otherwise.
     * 
     * @param entityName the name of the entity.
     * @param context    the {@link GenerationContext} containing configuration and context data.
     * @return {@code true} if the entity is irrelevant, {@code false} otherwise.
     * @since 1.0
     */
    private boolean isIrrelevantEntity(String entityName, GenerationContext context) {
        return isLibraryEntity(entityName) || !isProjectEntity(entityName, context);
    }

    /**
     * Determines if an entity belongs to a library.
     * 
     * Responsibilities:
     * - Checks if the entity name starts with common library prefixes such as "java.", "javax.", or "org.springframework.".
     * - Identifies entities that are part of standard libraries or frameworks.
     * 
     * Preconditions:
     * - The entity name must not be null.
     * 
     * Postconditions:
     * - Returns {@code true} if the entity belongs to a library, {@code false} otherwise.
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
     * - Checks if the entity name starts with the package specified in the
     * "include.package" configuration property.
     * - If "include.package" is not set or is empty, all entities are considered
     * part of the project.
     * 
     * Preconditions:
     * - The entity name must not be null.
     * - The {@link GenerationContext} must provide the "include.package"
     * configuration property.
     * 
     * Postconditions:
     * - Returns {@code true} if the entity belongs to the project, {@code false}
     * otherwise.
     * 
     * @param entityName the name of the entity.
     * @param context    the {@link GenerationContext} containing configuration and
     *                   context data.
     * @return {@code true} if the entity belongs to the project, {@code false}
     *         otherwise.
     * @since 1.0
     */
    private boolean isProjectEntity(String entityName, GenerationContext context) {
        String projectPackage = context.getIncludePackage();
        if (projectPackage == null || projectPackage.isEmpty()) {
            // If "include.package" is not set, include all entities
            return true;
        }
        return entityName.startsWith(projectPackage);
    }

     /**
     * Parses a list of Java source files and groups the extracted {@link CodeEntity} objects into {@link PackageEntity} objects.
     * 
     * Responsibilities:
     * - Parses the provided files to extract {@link CodeEntity} objects using the {@link #parseFiles} method.
     * - Groups the extracted {@link CodeEntity} objects by their package names.
     * - Creates a map of package names to {@link PackageEntity} objects, where each package contains its respective classes.
     * 
     * Preconditions:
     * - The input files must be valid `.java` files.
     * - The {@link GenerationContext} must provide the necessary configuration for filtering.
     * - The {@link ProgressTracker} must be initialized to track parsing progress.
     * 
     * Postconditions:
     * - Returns a map of package names to {@link PackageEntity} objects.
     * - Each {@link PackageEntity} contains the classes belonging to its package.
     * 
     * @param files          The list of file paths to parse.
     * @param context        The {@link GenerationContext} containing configuration and context data.
     * @param progressTracker The {@link ProgressTracker} to track parsing progress.
     * @return A map of package names to {@link PackageEntity} objects.
     * @since 1.0
     */
    public Map<String, PackageEntity> parsePackages(List<String> files, GenerationContext context, ProgressTracker progressTracker) {
        // Step 1: Parse files to extract CodeEntity objects
        List<CodeEntity> codeEntities = parseFiles(files, context, progressTracker);

        // Step 2: Group CodeEntity objects by package name
        Map<String, PackageEntity> packageMap = new HashMap<>();
        for (CodeEntity codeEntity : codeEntities) {
            String packageName = extractPackageName(codeEntity.getName());
            packageMap.computeIfAbsent(packageName, PackageEntity::new).addClass(codeEntity);
        }

        // Step 3: Return the map of package entities
        return packageMap;
    }


    /**
     * Extracts the package name from a fully qualified class name.
     * 
     * Responsibilities:
     * - Identifies the last occurrence of the '.' character in the fully qualified name.
     * - Extracts the substring before the last '.' as the package name.
     * - Returns an empty string if no '.' is found, indicating a default package.
     * 
     * Preconditions:
     * - The fully qualified name must not be null.
     * 
     * Postconditions:
     * - Returns the package name as a string.
     * - If the class is in the default package, an empty string is returned.
     * 
     * @param fullyQualifiedName The fully qualified name of the class.
     * @return The package name as a string, or an empty string if the class is in the default package.
     * @since 1.0
     */
    private String extractPackageName(String fullyQualifiedName) {
        int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fullyQualifiedName.substring(0, lastDotIndex);
    }

    /**
     * Logs any unresolved symbols encountered during parsing.
     * If there are unresolved symbols, they are retrieved from the 
     * thread-local storage and logged as warnings.
     * This method helps in identifying symbols that could not be resolved 
     * during the parsing process.
     */
    private void logUnresolvedSymbols() {

        List<String> symbols = unresolvedSymbols.get();
        if (!symbols.isEmpty()) {
            logger.warn("Unresolved symbols encountered during parsing:");
            symbols.forEach(symbol -> logger.warn(" - {}", symbol));
        }
    }

    /**
     * Adds a symbol to the list of unresolved symbols.
     *
     * @param symbol the name of the symbol to be added to the unresolved symbols list
     */
    private void addUnresolvedSymbol(String symbol) {
        unresolvedSymbols.get().add(symbol);
    }

}