package com.pjsoft.j2arch.docgen.javadoc.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocHtmlGenerator;

/**
 * JavaDocPackagePageGenerator
 * 
 * Responsible for generating HTML documentation for individual packages. This
 * class uses templates to create package-level documentation, including details
 * about classes within the package and related packages.
 * 
 * Responsibilities:
 * - Generate HTML documentation for a given package.
 * - Prepare placeholders for the package template.
 * - Write the generated HTML content to the output directory.
 * 
 * Dependencies:
 * - {@link PackageEntity}: Represents metadata about a package.
 * - {@link JavaDocGenerationContext}: Provides configuration details for documentation generation.
 * - {@link JavaDocHtmlGenerator}: Utility for generating and writing HTML content.
 * 
 * Limitations:
 * - Assumes that the provided package entity and context are valid and non-null.
 * - Relies on the correctness of the HTML template and placeholders.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it does not manage concurrent access.
 * 
 * Usage Example:
 * {@code
 * PackageEntity packageEntity = ...;
 * JavaDocGenerationContext context = ...;
 * JavaDocPackagePageGenerator generator = new JavaDocPackagePageGenerator();
 * generator.generatePackagePage(packageEntity, context);
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocPackagePageGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaDocPackagePageGenerator.class);

    /**
     * Generates the HTML documentation for a given package.
     * 
     * Responsibilities:
     * - Loads the package HTML template.
     * - Prepares placeholders with package-specific details.
     * - Generates HTML content using the template and placeholders.
     * - Writes the generated HTML content to the output directory.
     * 
     * @param packageEntity The package entity containing information about the package.
     * @param context       The context containing paths and configuration for documentation generation.
     * @throws IOException If an error occurs during HTML generation or file writing.
     */
    public void generatePackagePage(PackageEntity packageEntity, JavaDocGenerationContext context) throws IOException {
        // Step 1: Load the template path from the context
        String templatePath = context.getPackageTemplateFile();

        // Step 2: Prepare placeholders for the template
        Map<String, String> placeholders = preparePlaceholders(packageEntity, context);

        // Step 3: Generate HTML content using JavaDocHtmlGenerator
        String htmlContent = JavaDocHtmlGenerator.generateHtml(templatePath, placeholders);

        // Step 4: Write the generated HTML content to the output directory
        String outputPath = context.getHtmlDirectory() + File.separator + packageEntity.getName().replace(".", "_")
                + ".html";
        JavaDocHtmlGenerator.writeHtmlFile(outputPath, htmlContent);

        logger.debug("Generated package documentation for: {} ", packageEntity.getName());
    }

    /**
     * Prepares the placeholders for the package HTML template.
     * 
     * Responsibilities:
     * - Generates a list of classes within the package.
     * - Generates a list of related packages.
     * - Prepares a map of placeholders and their corresponding values.
     * 
     * @param packageEntity The package entity containing information about the package.
     * @param context       The context containing paths and configuration for documentation generation.
     * @return A map of placeholders and their corresponding values.
     */
    private Map<String, String> preparePlaceholders(PackageEntity packageEntity, JavaDocGenerationContext context) {
        // Generate the list of classes
        StringBuilder classesList = new StringBuilder();
        for (CodeEntity codeEntity : packageEntity.getClasses()) {
            classesList.append("<li><a href=\"")
                    .append(codeEntity.getName().replace(".", "_"))
                    .append(".html\">")
                    .append(codeEntity.getName())
                    .append("</a></li>");
        }

        // Generate the list of related packages
        StringBuilder relatedPackagesList = new StringBuilder();
        for (PackageEntity relatedPackage : packageEntity.getRelatedPackages()) {
            relatedPackagesList.append("<li><a href=\"")
                    .append(relatedPackage.getName().replace(".", "_"))
                    .append(".html\">")
                    .append(relatedPackage.getName())
                    .append("</a></li>");
        }

        // Prepare placeholders
        return Map.of(
                "{{packageName}}", packageEntity.getName(),
                "{{packageDiagram}}",
                packageEntity.getPackageDiagram() != null ? packageEntity.getPackageDiagram()
                        : context.getImagesOutputDirectory() + "/placeholder.png",
                "{{classes}}", classesList.toString(),
                "{{relatedPackages}}", relatedPackagesList.toString());
    }
}