package com.pjsoft.j2arch.docgen.javadoc.service;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.FieldEntity;
import com.pjsoft.j2arch.core.model.MethodEntity;
import com.pjsoft.j2arch.core.model.Relative;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocHtmlGenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JavaDocClassPageGenerator
 * 
 * Responsible for generating HTML documentation for individual classes. This
 * class uses templates to create class-level documentation, including details
 * about fields, methods, constructors, and relationships.
 * 
 * Responsibilities:
 * - Generate HTML documentation for a given class.
 * - Prepare placeholders for the class template.
 * - Write the generated HTML content to the output directory.
 * 
 * Dependencies:
 * - {@link CodeEntity}: Represents metadata about a class.
 * - {@link JavaDocGenerationContext}: Provides configuration details for documentation generation.
 * - {@link JavaDocHtmlGenerator}: Utility for generating and writing HTML content.
 * 
 * Limitations:
 * - Assumes that the provided class entity and context are valid and non-null.
 * - Relies on the correctness of the HTML template and placeholders.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it does not manage concurrent access.
 * 
 * Usage Example:
 * {@code
 * CodeEntity codeEntity = ...;
 * JavaDocGenerationContext context = ...;
 * JavaDocClassPageGenerator generator = new JavaDocClassPageGenerator();
 * generator.generateClassPage(codeEntity, context);
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocClassPageGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaDocClassPageGenerator.class);

    /**
     * Generates the HTML documentation for a given class.
     * 
     * Responsibilities:
     * - Loads the class HTML template.
     * - Prepares placeholders with class-specific details.
     * - Generates HTML content using the template and placeholders.
     * - Writes the generated HTML content to the output directory.
     * 
     * @param codeEntity The class entity containing information about the class.
     * @param context    The context containing paths and configuration for documentation generation.
     * @throws IOException If an error occurs during HTML generation or file writing.
     */
    public void generateClassPage(CodeEntity codeEntity, JavaDocGenerationContext context) throws IOException {
        // Step 1: Load the template path from the context
        String templatePath = context.getClassTemplateFile();

        // Step 2: Prepare placeholders for the template
        Map<String, String> placeholders = preparePlaceholders(codeEntity);

        // Step 3: Generate HTML content using JavaDocHtmlGenerator
        String htmlContent = JavaDocHtmlGenerator.generateHtml(templatePath, placeholders);

        // Step 4: Write the generated HTML content to the output directory
        String outputPath = context.getHtmlDirectory() + File.separator + codeEntity.getName().replace(".", "_")
                + ".html";
        JavaDocHtmlGenerator.writeHtmlFile(outputPath, htmlContent);

        logger.debug("Generated class documentation for: " + codeEntity.getName());
    }

    /**
     * Prepares the placeholders for the class HTML template.
     * 
     * Responsibilities:
     * - Generates placeholders for class annotations, fields, constructors, methods, and relationships.
     * - Prepares a map of placeholders and their corresponding values.
     * 
     * @param codeEntity The class entity containing information about the class.
     * @return A map of placeholders and their corresponding values.
     */
    private Map<String, String> preparePlaceholders(CodeEntity codeEntity) {
        // Generate class annotations
        String classAnnotations = String.join("<br>", codeEntity.getAnnotations());

        // Generate the table rows for constructors
        StringBuilder constructorsTable = new StringBuilder();
        for (MethodEntity constructor : codeEntity.getConstructors()) {
            String parameters = String.join(", ", constructor.getParameters());
            String annotations = String.join(", ", constructor.getAnnotations());
            constructorsTable.append("<tr>")
                    .append("<td>").append(constructor.getVisibility()).append("</td>")
                    .append("<td>").append(constructor.getName()).append("</td>")
                    .append("<td>").append(parameters).append("</td>")
                    .append("<td>").append(annotations).append("</td>")
                    .append("<td>").append("Description placeholder").append("</td>") // Placeholder for description
                    .append("</tr>");
        }

        // Generate the table rows for methods
        StringBuilder methodsTable = new StringBuilder();
        for (MethodEntity method : codeEntity.getMethods()) {
            String parameters = String.join(", ", method.getParameters());
            String annotations = String.join(", ", method.getAnnotations());
            methodsTable.append("<tr>")
                    .append("<td>").append(method.getVisibility()).append("</td>")
                    .append("<td>").append(method.getReturnType()).append("</td>")
                    .append("<td>").append(method.getName()).append("(").append(parameters).append(")").append("</td>")
                    .append("<td>").append(annotations).append("</td>")
                    .append("<td>").append("Description placeholder").append("</td>") // Placeholder for description
                    .append("</tr>");
        }

        // Generate the table rows for fields
        StringBuilder fieldsTable = new StringBuilder();
        for (FieldEntity field : codeEntity.getFields()) {
            String annotations = String.join(", ", field.getAnnotations());
            fieldsTable.append("<tr>")
                    .append("<td>").append(field.getVisibility()).append("</td>")
                    .append("<td>").append(field.getType()).append("</td>")
                    .append("<td>").append(field.getName()).append("</td>")
                    .append("<td>").append(annotations).append("</td>")
                    .append("<td>").append("Description placeholder").append("</td>") // Placeholder for description
                    .append("</tr>");
        }

        // Generate the table rows for relationships
        StringBuilder relationshipsTable = new StringBuilder();
        Set<String> uniqueRelationships = new HashSet<>(); // Track unique relationships

        for (Relative relative : codeEntity.getRelatives()) {
            // Create a unique key for the relationship
    String relationshipKey = relative.getRelationshipType() + ":" + relative.getCalleeEntity().getName();

            // Add the relationship only if it's unique
            if (uniqueRelationships.add(relationshipKey)) {
            relationshipsTable.append("<tr>")
                    .append("<td>").append(relative.getRelationshipType()).append("</td>")
                    .append("<td>").append(relative.getCalleeEntity().getName()).append("</td>")
                    .append("<td>").append(relative.getDetails()).append("</td>") // Add details if available
                    .append("</tr>");
            }
        }

        // Prepare placeholders
        return Map.of(
                "{{className}}", codeEntity.getName(),
                "{{classAnnotations}}", classAnnotations,
                "{{fields}}", fieldsTable.toString(),
                "{{constructors}}", constructorsTable.toString(),
                "{{methods}}", methodsTable.toString(),
                "{{relationships}}", relationshipsTable.toString(),
                "{{classDiagram}}",
                codeEntity.getClassDiagram() != null ? codeEntity.getClassDiagram() : "placeholder.png");
    }
}
