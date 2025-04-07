package com.pjsoft.j2arch.uml.config;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;

/**
 * Configures the JavaSymbolSolver for resolving types and symbols in JavaParser.
 * This class sets up the symbol solver with the project's source files and the JDK.
 *
 * Responsibilities:
 * - Adds the JDK and project source files to the CombinedTypeSolver.
 * - Configures the StaticJavaParser with the symbol solver.
 *
 * Usage Example:
 * {@code SymbolSolverConfig.configureSymbolSolver("src/main/java");}
 *
 * Dependencies:
 * - JavaParser
 * - JavaSymbolSolver
 *
 * Thread Safety:
 * - This class is thread-safe as it only configures static components.
 *
 * Limitations:
 * - The input source root path must be valid and point to a directory.
 *
 * Author: JagsProgramming
 * Since: 1.0
 */
public class SymbolSolverConfig {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SymbolSolverConfig.class);

    /**
     * Configures the JavaSymbolSolver with the given source root path.
     * This method sets up the CombinedTypeSolver with the JDK and the project's source files,
     * and attaches the symbol solver to the StaticJavaParser.
     *
     * Preconditions:
     * - The input source root path must be valid and point to a directory.
     *
     * Postconditions:
     * - The StaticJavaParser is configured with the symbol solver.
     *
     * Usage Example:
     * {@code SymbolSolverConfig.configureSymbolSolver("src/main/java");}
     *
     * @param inputSourceRootPath the root path of the project's source files.
     * @throws IllegalArgumentException if the input source root path is invalid.
     * @since 1.0
     */
    public static void configureSymbolSolver(String inputSourceRootPath) {
       
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // Add the JDK to the TypeSolver
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Add the input project's source files to the TypeSolver
        File inputSourceRoot = new File(inputSourceRootPath);
        logger.debug("inputSourceRootPath in SymbolSolverConfig: " + inputSourceRootPath);
        if (!inputSourceRoot.exists() || !inputSourceRoot.isDirectory()) {
            throw new IllegalArgumentException("Invalid input source root path: " + inputSourceRootPath);
        }
        combinedTypeSolver.add(new JavaParserTypeSolver(inputSourceRoot));
        // Add the classpath for external libraries
        combinedTypeSolver.add(new ClassLoaderTypeSolver(Thread.currentThread().getContextClassLoader()));
        // Attach the SymbolSolver to the StaticJavaParser
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);
        StaticJavaParser.setConfiguration(parserConfiguration);

        logger.info("Symbol Solver configured with input source root: " + inputSourceRootPath);
    }
}