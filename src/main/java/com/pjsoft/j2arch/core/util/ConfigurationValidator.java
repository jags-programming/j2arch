package com.pjsoft.j2arch.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.core.context.GenerationContext;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.pumldoc.util.HTMLGenerationContext;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * ConfigurationValidator
 * 
 * Utility class for validating and preparing directories and files used in the
 * application. This class ensures that required directories and files exist,
 * creating directories if necessary, and validating the presence of files in
 * the file system or classpath.
 * 
 * Responsibilities:
 * - Validate the existence of directories and files.
 * - Create directories if they do not exist.
 * - Validate files in the classpath or file system.
 * - Log validation results for debugging purposes.
 * 
 * Limitations:
 * - Assumes that the provided paths are valid and accessible.
 * - Throws exceptions if directories cannot be created or files are missing.
 * 
 * Usage Example:
 * {@code
 * UMLGenerationContext context = ...;
 * ConfigurationValidator.validateContext(context);
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class ConfigurationValidator {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationValidator.class);

    /**
     * Validates that the given directory exists. If it doesn't, attempts to create
     * it.
     * 
     * Responsibilities:
     * - Check if the directory exists.
     * - Create the directory if it does not exist.
     * - Log the result of the validation.
     * 
     * @param directoryPath The path of the directory to validate.
     * @throws IOException If the directory cannot be created.
     */
    public static void validateOrCreateDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            logger.warn("Directory does not exist: {}. Attempting to create it.", directoryPath);
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directoryPath);
            }
            logger.debug("Directory created successfully: {}", directoryPath);
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException("The path exists but is not a directory: " + directoryPath);
        } else {
            logger.debug("Directory validated successfully: {}", directoryPath);
        }
    }

    /**
     * Validates that the given directory exists and is a directory.
     * 
     * Responsibilities:
     * - Check if the directory exists and is a valid directory.
     * - Log the result of the validation.
     * 
     * @param directoryPath The path of the directory to validate.
     * @throws IOException If the directory does not exist or is not a directory.
     */
    public static void validateDirectory(String directoryPath) throws IOException {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty.");
        }

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        if (!directory.isDirectory()) {
            throw new IOException("The path exists but is not a directory: " + directoryPath);
        }
        logger.debug("Directory validated successfully: {}", directoryPath);
    }

    /**
     * Validates that the given file exists in the classpath.
     * 
     * Responsibilities:
     * - Check if the file exists in the classpath.
     * - Log the result of the validation.
     * 
     * @param filePath The path of the file to validate.
     * @throws IOException If the file is not found in the classpath.
     */
    public static void validateFileInClasspath(String filePath) throws IOException {
        try (InputStream resourceStream = ConfigurationValidator.class.getResourceAsStream(filePath)) {
            if (resourceStream == null) {
                throw new IOException("File not found in classpath: " + filePath);
            }
            logger.debug("File validated successfully in classpath: {}", filePath);
        }
    }

    /**
     * Validates that the given file exists in the file system.
     * 
     * Responsibilities:
     * - Check if the file exists and is a valid file.
     * - Log the result of the validation.
     * 
     * @param filePath The path of the file to validate.
     * @throws IOException If the file does not exist or is not a valid file.
     */
    public static void validateFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File not found: " + filePath);
        }
        logger.debug("File validated successfully: {}", filePath);
    }

    /**
     * Validates all directories and files specified in the UMLGenerationContext.
     * Creates directories if they do not exist.
     * 
     * Responsibilities:
     * - Validate or create required directories.
     * - Log the validation results.
     * 
     * @param context The UMLGenerationContext containing paths to validate.
     * @throws IOException If a directory cannot be created or a file is missing.
     */
    public static void validateContext(UMLGenerationContext context) throws IOException {
        validateDirectory(context.getInputDirectory());
        validateOrCreateDirectory(context.getOutputDirectory());
        validateOrCreateDirectory(context.getPumlPath());
        validateOrCreateDirectory(context.getImagesOutputDirectory());
        logger.info("All directories and files in the context have been validated successfully.");
    }

    /**
     * Validates all directories and files specified in the HTMLGenerationContext.
     * Creates directories if they do not exist.
     * 
     * Responsibilities:
     * - Validate or create required directories.
     * - Validate required templates in the classpath.
     * - Log the validation results.
     * 
     * @param context The HTMLGenerationContext containing paths to validate.
     * @throws IOException If a directory cannot be created or a file is missing.
     */
    public static void validateContext(HTMLGenerationContext context) throws IOException {
        validateDirectory(context.getInputDirectory());
        validateOrCreateDirectory(context.getOutputDirectory());
        validateOrCreateDirectory(PathResolver.resolvePath(context.getOutputDirectory(), DirectoryConstants.STYLES_DIR));
        validateOrCreateDirectory(context.getImagesOutputDirectory());
        validateFileInClasspath(context.getIndexTemplateFile());
        validateFileInClasspath(context.getDiagramTemplateFile());
        logger.info("All directories and files in the context have been validated successfully.");
    }

    /**
     * Validates all directories and files specified in the JavaDocGenerationContext.
     * Creates directories if they do not exist.
     * 
     * Responsibilities:
     * - Validate or create required directories.
     * - Validate required templates in the classpath.
     * - Log the validation results.
     * 
     * @param context The JavaDocGenerationContext containing paths to validate.
     * @throws IOException If a directory cannot be created or a file is missing.
     */
    public static void validateContext(JavaDocGenerationContext context) throws IOException {
        validateDirectory(context.getInputDirectory());
        validateOrCreateDirectory(context.getOutputDirectory());
        validateOrCreateDirectory(context.getPumlPath());
        validateOrCreateDirectory(context.getHtmlDirectory());
        validateOrCreateDirectory(context.getHtmlDirectory() + File.separator + DirectoryConstants.STYLES_DIR);
        validateOrCreateDirectory(context.getImagesOutputDirectory());
        validateFileInClasspath(context.getIndexTemplateFile());
        validateFileInClasspath(context.getClassTemplateFile());
        validateFileInClasspath(context.getPackageTemplateFile());
        logger.info("All directories and files in the context have been validated successfully.");
    }
}
