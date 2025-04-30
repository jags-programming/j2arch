package com.pjsoft.j2arch.docgen.javadoc.service;

import java.io.IOException;
import java.util.Map;

import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocHtmlGenerator;

/**
 * JavaDocIndexPageGenerator
 * 
 * Responsible for generating the main index page for the Javadoc-like
 * documentation. This class creates an overview page that links to all
 * package-level and class-level documentation.
 * 
 * Responsibilities:
 * - Generate the index HTML page for the documentation.
 * - Prepare placeholders for the index template.
 * - Write the generated HTML content to the output directory.
 * 
 * Dependencies:
 * - {@link PackageEntity}: Represents metadata about a package.
 * - {@link JavaDocGenerationContext}: Provides configuration details for documentation generation.
 * - {@link JavaDocHtmlGenerator}: Utility for generating and writing HTML content.
 * 
 * Limitations:
 * - Assumes that the provided package entities and context are valid and non-null.
 * - Relies on the correctness of the HTML template and placeholders.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it does not manage concurrent access.
 * 
 * Usage Example:
 * {@code
 * Map<String, PackageEntity> packageEntities = ...;
 * JavaDocGenerationContext context = ...;
 * JavaDocIndexPageGenerator generator = new JavaDocIndexPageGenerator();
 * generator.generateIndexPage(packageEntities, context);
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocIndexPageGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaDocIndexPageGenerator.class);

    /**
     * Generates the HTML content for the index page and writes it to the output
     * directory.
     * 
     * Responsibilities:
     * - Loads the index HTML template.
     * - Prepares placeholders with package and class details.
     * - Generates HTML content using the template and placeholders.
     * - Writes the generated HTML content to the output directory.
     * 
     * @param packageEntities A map of package names to {@link PackageEntity} objects.
     * @param context         The {@link JavaDocGenerationContext} containing paths and configuration for
     *                        documentation generation.
     * @throws IOException If an error occurs during HTML generation or file writing.
     */
    public void generateIndexPage(Map<String, PackageEntity> packageEntities, JavaDocGenerationContext context)
            throws IOException {
        // Step 1: Load the template path from the context
        String templatePath = context.getIndexTemplateFile();

        // Step 2: Prepare placeholders for the template
        Map<String, String> placeholders = preparePlaceholders(packageEntities);

        // Step 3: Generate HTML content using JavaDocHtmlGenerator
        String htmlContent = JavaDocHtmlGenerator.generateHtml(templatePath, placeholders);

        // Step 4: Write the generated HTML content to the output directory
        String outputDir = context.getHtmlDirectory();
        String outputPath = outputDir + "/index.html";
        JavaDocHtmlGenerator.writeHtmlFile(outputPath, htmlContent);

        logger.info("Generated index page for the documentation.");
    }

    /**
     * Prepares the placeholders for the index HTML template.
     * 
     * Responsibilities:
     * - Generates a table of packages and their associated classes.
     * - Prepares a map of placeholders and their corresponding values.
     * 
     * @param packageEntities A map of package names to {@link PackageEntity} objects.
     * @return A map of placeholders and their corresponding values.
     */
    private Map<String, String> preparePlaceholders(Map<String, PackageEntity> packageEntities) {
        // Generate the table rows for packages and their classes
        StringBuilder packagesAndClassesTable = new StringBuilder();
        for (PackageEntity packageEntity : packageEntities.values()) {
            // Generate the list of classes for the package
            String classes = packageEntity.getClasses().stream()
                    .map(codeEntity -> "<a href=\"" + codeEntity.getName().replace(".", "_") + ".html\">"
                            + codeEntity.getName() + "</a>")
                    .reduce((a, b) -> a + ", " + b) // Comma-separated list
                    .orElse("No classes");

            // Add a table row for the package
            packagesAndClassesTable.append("<tr>")
                    .append("<td><a href=\"").append(packageEntity.getName().replace(".", "_")).append(".html\">")
                    .append(packageEntity.getName()).append("</a></td>")
                    .append("<td>").append(classes).append("</td>")
                    .append("</tr>");
        }

        // Prepare placeholders
        return Map.of(
                "{{packagesAndClasses}}", packagesAndClassesTable.toString());
    }
}
