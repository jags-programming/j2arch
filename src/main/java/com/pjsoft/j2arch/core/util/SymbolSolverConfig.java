package com.pjsoft.j2arch.core.util;

import java.io.File;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.pjsoft.j2arch.core.context.GenerationContext;

/**
 * SymbolSolverConfig
 * 
 * Configures the JavaSymbolSolver for resolving types and symbols in JavaParser.
 * This class sets up the symbol solver with the project's source files, the JDK,
 * and external libraries.
 * 
 * Responsibilities:
 * - Adds the JDK and project source files to the CombinedTypeSolver.
 * - Adds external libraries from the `libs/` directory to the CombinedTypeSolver.
 * - Configures the StaticJavaParser with the symbol solver.
 * 
 * Dependencies:
 * - JavaParser: For parsing and analyzing Java source code.
 * - JavaSymbolSolver: For resolving types and symbols in Java code.
 * 
 * Limitations:
 * - The input source root path must be valid and point to a directory.
 * - Assumes that the `libs/` directory contains valid JAR files for external libraries.
 * 
 * Thread Safety:
 * - This class is thread-safe as it only configures static components.
 * 
 * Usage Example:
 * {@code
 * GenerationContext context = ...;
 * SymbolSolverConfig.configureSymbolSolver("src/main/java", context);
 * }
 * 
 * Author: JagsProgramming
 * Since: 1.0
 */
public class SymbolSolverConfig {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SymbolSolverConfig.class);

    /**
     * Configures the JavaSymbolSolver with the given source root path.
     * Automatically includes the JDK, project source files, and external libraries
     * from the `libs/` directory.
     * 
     * Responsibilities:
     * - Adds the JDK to the CombinedTypeSolver.
     * - Adds the project's source files to the CombinedTypeSolver.
     * - Adds external libraries from the `libs/` directory to the CombinedTypeSolver.
     * - Configures the StaticJavaParser with the symbol solver.
     * 
     * @param inputSourceRootPath The root path of the project's source files.
     * @param context             The {@link GenerationContext} containing configuration details.
     * @throws IllegalArgumentException If the input source root path is invalid.
     */
    public static void configureSymbolSolver(String inputSourceRootPath, GenerationContext context) {
        String libsDirPath = context.getLibsDirPath();

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // Add the JDK to the TypeSolver
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Add the input project's source files to the TypeSolver
        File inputSourceRoot = new File(inputSourceRootPath);
        logger.debug("inputSourceRootPath in SymbolSolverConfig: {}", inputSourceRootPath);
        if (!inputSourceRoot.exists() || !inputSourceRoot.isDirectory()) {
            throw new IllegalArgumentException("Invalid input source root path: " + inputSourceRootPath);
        }
        combinedTypeSolver.add(new JavaParserTypeSolver(inputSourceRoot));

        // Add external libraries from the `libs/` directory
        addExternalLibraries(combinedTypeSolver, libsDirPath);

        // Attach the SymbolSolver to the StaticJavaParser
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);
        StaticJavaParser.setConfiguration(parserConfiguration);

        logger.debug("Symbol Solver configured with input source root: {}", inputSourceRootPath);
    }

    /**
     * Adds external libraries from the `libs/` directory to the CombinedTypeSolver.
     * 
     * Responsibilities:
     * - Scans the `libs/` directory for JAR files.
     * - Adds each JAR file to the CombinedTypeSolver.
     * 
     * @param combinedTypeSolver The CombinedTypeSolver to which the libraries will be added.
     * @param libsDirPath        The path to the `libs/` directory containing external libraries.
     */
    private static void addExternalLibraries(CombinedTypeSolver combinedTypeSolver, String libsDirPath) {
        File libsDir = new File(libsDirPath);
        if (libsDir.exists() && libsDir.isDirectory()) {
            File[] jarFiles = libsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    try {
                        combinedTypeSolver.add(
                                new com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver(jarFile));
                        logger.debug("Added external library: {}", jarFile.getAbsolutePath());
                    } catch (Exception e) {
                        logger.warn("Failed to add library: {}", jarFile.getAbsolutePath(), e.getMessage());
                    }
                }
            }
        } else {
            logger.warn("No external libraries found. Directory `libs/` does not exist or is empty.");
        }
    }
}