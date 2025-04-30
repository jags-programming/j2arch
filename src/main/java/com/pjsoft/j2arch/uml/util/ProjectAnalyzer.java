package com.pjsoft.j2arch.uml.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.core.context.GenerationContext;
import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.core.util.JavaParserService;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.SymbolSolverConfig;

/**
 * Analyzes a Java project to extract information for UML diagram generation.
 * 
 * Responsibilities:
 * - Configures the symbol solver for type resolution.
 * - Recursively collects `.java` files from the input directory.
 * - Parses the collected files to extract code entities and package
 * information.
 * - Identifies duplicate file names in the project.
 * - Tracks progress using the {@link ProgressTracker}.
 * 
 * Usage Example:
 * {@code
 * GenerationContext context = new GenerationContext(...);
 * ProgressTracker progressTracker = new ProgressTracker(...);
 * ProjectAnalyzer analyzer = new ProjectAnalyzer();
 * analyzer.configureSymbolSolver(context);
 * List<CodeEntity> entities = analyzer.analyzeProject(context, progressTracker);
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}: Provides configuration details for the
 * project.
 * - {@link JavaParserService}: Parses `.java` files to extract code entities.
 * - {@link SymbolSolverConfig}: Configures the symbol solver for type
 * resolution.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state.
 * 
 * Limitations:
 * - Assumes that the input directory is valid and contains `.java` files.
 * - Requires a valid configuration with the input directory specified.
 * - Filters classes based on the "include.package" configuration property.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class ProjectAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAnalyzer.class);

    /**
     * Constructs a new ProjectAnalyzer.
     * 
     * Responsibilities:
     * - Initializes the required services for project analysis.
     */
    public ProjectAnalyzer() {
        // No explicit initialization required for now
    }

    /**
     * Configures the symbol solver with the input directory specified in the
     * context.
     * 
     * Responsibilities:
     * - Reads the input directory path from the context.
     * - Configures the symbol solver for type resolution using the
     * {@link SymbolSolverConfig}.
     * 
     * Preconditions:
     * - The input directory must be specified in the context.
     * 
     * Postconditions:
     * - The symbol solver is configured for type resolution.
     * 
     * @param context The generation context containing configuration details.
     * @throws IllegalArgumentException if the input directory is not specified or
     *                                  invalid.
     */
    public void configureSymbolSolver(GenerationContext context) {
        String inputSourceRootPath = context.getInputDirectory();
        if (inputSourceRootPath == null || inputSourceRootPath.isEmpty()) {
            throw new IllegalArgumentException("Input directory is not specified in the configuration.");
        }
        SymbolSolverConfig.configureSymbolSolver(inputSourceRootPath, context);
    }

    /**
     * Recursively collects all `.java` files from the input directory specified in
     * the context.
     * 
     * Responsibilities:
     * - Validates the input directory.
     * - Recursively searches for `.java` files in the directory and its
     * subdirectories.
     * - Tracks progress using the {@link ProgressTracker}.
     * 
     * Preconditions:
     * - The input directory must be specified in the context.
     * 
     * Postconditions:
     * - A list of `.java` file paths is returned.
     * 
     * @param context         The generation context containing configuration
     *                        details.
     * @param progressTracker The progress tracker to monitor progress.
     * @return A list of file paths for all `.java` files.
     * @throws IllegalArgumentException if the input directory is not specified or
     *                                  invalid.
     */
    public List<String> collectJavaFiles(GenerationContext context, ProgressTracker progressTracker) {
        String directoryPath = context.getInputDirectory();
        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new IllegalArgumentException("Input directory is not specified in the configuration.");
        }

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Input directory does not exist or is not a directory: " + directoryPath);
        }

        List<String> files = new ArrayList<>();
        AtomicInteger directoryCount = new AtomicInteger(0);

        collectJavaFiles(directory, files, directoryCount);

        // Initialize progress tracker for units
        progressTracker.initializeTaskCounts(files.size(), directoryCount.get());
        progressTracker.onStatusUpdate("Total files to parse: " + files.size());
        return files;
    }

    /**
     * Recursively collects `.java` files from the specified directory.
     * 
     * Responsibilities:
     * - Traverses the directory structure to find `.java` files.
     * 
     * @param directory      The directory to search.
     * @param files          The list to store the file paths.
     * @param directoryCount The counter to track the number of directories
     *                       traversed.
     */
    private void collectJavaFiles(File directory, List<String> files, AtomicInteger directoryCount) {
        if (directory.isDirectory()) {
            directoryCount.incrementAndGet(); // Increment the directory count
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    collectJavaFiles(file, files, directoryCount); // Recurse into subdirectories
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
     * - A list of {@link CodeEntity} objects is returned, representing parsed
     * classes.
     * 
     * @param context         The generation context containing configuration
     *                        details.
     * @param progressTracker The progress tracker to monitor progress.
     * @return A list of {@link CodeEntity} objects representing parsed classes.
     */
    public List<CodeEntity> analyzeProject(GenerationContext context, ProgressTracker progressTracker) {
        // Collect Java files from the input directory
        List<String> files = collectJavaFiles(context, progressTracker);

        JavaParserService parser = new JavaParserService();
        // Parse the files and return the extracted CodeEntity objects
        return parser.parseFiles(files, context, progressTracker);
    }

    /**
     * Parses the collected `.java` files and extracts {@link PackageEntity}
     * objects.
     * 
     * Responsibilities:
     * - Collects `.java` files from the input directory.
     * - Parses the files using the {@link JavaParserService}.
     * 
     * @param context         The generation context containing configuration
     *                        details.
     * @param progressTracker The progress tracker to monitor progress.
     * @return A map of package names to {@link PackageEntity} objects.
     */
    public Map<String, PackageEntity> analyzeProjectForPackages(GenerationContext context,
            ProgressTracker progressTracker) {
        // Collect Java files from the input directory
        List<String> files = collectJavaFiles(context, progressTracker);

        // Parse the files and return the extracted PackageEntity objects
        JavaParserService parser = new JavaParserService();
        return parser.parsePackages(files, context, progressTracker);
    }

    /**
     * Identifies duplicate file names in the provided list of file paths.
     * 
     * Responsibilities:
     * - Extracts file names from the provided file paths.
     * - Counts occurrences of each file name.
     * - Collects file names that appear more than once.
     * 
     * @param filePaths The list of file paths to analyze.
     * @return A list of duplicate file names.
     */
    public List<String> findDuplicateFileNames(List<String> filePaths) {
        // Map to store file name counts
        Map<String, Integer> fileNameCounts = new HashMap<>();

        // Count occurrences of each file name
        for (String filePath : filePaths) {
            String fileName = new File(filePath).getName(); // Extract file name
            fileNameCounts.put(fileName, fileNameCounts.getOrDefault(fileName, 0) + 1);
        }

        // Collect file names with count > 1
        List<String> duplicates = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : fileNameCounts.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry.getKey());
            }
        }

        return duplicates;
    }

    /**
     * Analyzes the project for duplicate file names and logs the results.
     * 
     * Responsibilities:
     * - Collects `.java` files from the input directory.
     * - Identifies duplicate file names.
     * - Logs the results and updates the progress tracker.
     * 
     * @param context         The generation context containing configuration
     *                        details.
     * @param progressTracker The progress tracker to monitor progress.
     */
    public void analyzeForDuplicates(GenerationContext context, ProgressTracker progressTracker) {
        // Collect Java files
        List<String> files = collectJavaFiles(context, progressTracker);

        // Find duplicates
        List<String> duplicateFileNames = findDuplicateFileNames(files);

        // Log or handle duplicates
        if (!duplicateFileNames.isEmpty()) {
            logger.warn("Duplicate file names found: " + duplicateFileNames);
            progressTracker.onStatusUpdate("Duplicate file names found: " + duplicateFileNames);
        } else {
            logger.info("No duplicate file names found.");
            progressTracker.onStatusUpdate("No duplicate file names found.");
        }
    }
}
