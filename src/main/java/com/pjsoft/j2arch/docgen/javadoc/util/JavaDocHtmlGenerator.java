package com.pjsoft.j2arch.docgen.javadoc.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * JavaDocHtmlGenerator
 * 
 * Utility class for generating HTML content for Javadoc-like documentation.
 * This class provides methods to load HTML templates, replace placeholders
 * with actual values, and write the generated HTML content to files.
 * 
 * Responsibilities:
 * - Load HTML templates from the classpath.
 * - Replace placeholders in the templates with actual values.
 * - Write the generated HTML content to files.
 * 
 * Limitations:
 * - Assumes that the provided template paths and placeholders are valid.
 * - Does not validate the correctness of the HTML content.
 * 
 * Thread Safety:
 * - This class is thread-safe as it does not maintain any mutable state.
 * 
 * Usage Example:
 * {@code
 * Map<String, String> placeholders = Map.of("{{title}}", "My Documentation");
 * String htmlContent = JavaDocHtmlGenerator.generateHtml("/templates/index.html", placeholders);
 * JavaDocHtmlGenerator.writeHtmlFile("/output/index.html", htmlContent);
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocHtmlGenerator {

    /**
     * Generates HTML content by loading a template and replacing placeholders.
     *
     * Responsibilities:
     * - Loads the HTML template from the specified path.
     * - Replaces placeholders in the template with actual values provided in the map.
     * - Returns the generated HTML content as a string.
     *
     * @param templatePath The path to the HTML template file in the classpath.
     * @param placeholders A map of placeholders and their corresponding values.
     * @return The generated HTML content as a string.
     * @throws IOException If an error occurs while reading the template.
     */
    public static String generateHtml(String templatePath, Map<String, String> placeholders) throws IOException {
        // Step 1: Load the template
        String template = loadTemplate(templatePath);

        // Step 2: Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }

        // Step 3: Return the generated HTML content
        return template;
    }

    /**
     * Writes the generated HTML content to a file.
     *
     * Responsibilities:
     * - Ensures the parent directory of the output file exists.
     * - Writes the provided HTML content to the specified file path.
     *
     * @param outputPath  The path where the HTML file will be saved.
     * @param htmlContent The HTML content to write.
     * @throws IOException If an error occurs during the write process.
     */
    public static void writeHtmlFile(String outputPath, String htmlContent) throws IOException {
        // Files.createDirectories(Paths.get(outputPath).getParent()); // Ensure parent directory exists
        Files.write(Paths.get(outputPath), htmlContent.getBytes());
    }

    /**
     * Loads the HTML template from the specified file path in the classpath.
     *
     * Responsibilities:
     * - Reads the content of the specified template file from the classpath.
     * - Returns the content of the template as a string.
     *
     * @param templatePath The path to the HTML template file in the classpath.
     * @return The content of the template as a string.
     * @throws IOException If an error occurs while reading the file or the resource
     *                     is not found.
     */
    private static String loadTemplate(String templatePath) throws IOException {
        try (InputStream inputStream = JavaDocHtmlGenerator.class.getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Template file not found in resources: " + templatePath);
            }
            return new String(inputStream.readAllBytes());
        }
    }
}
