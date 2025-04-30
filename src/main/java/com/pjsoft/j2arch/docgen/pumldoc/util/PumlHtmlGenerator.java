package com.pjsoft.j2arch.docgen.pumldoc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Scanner;

import com.pjsoft.j2arch.docgen.pumldoc.PUML2HTMLDocGenerator;
import com.pjsoft.j2arch.docgen.pumldoc.model.DiagramInfo;
import com.pjsoft.j2arch.uml.service.ClassDiagramService;

/**
 * PumlHtmlGenerator
 * 
 * A utility class for generating HTML files for PlantUML diagrams. This class provides
 * methods to generate an index file linking all diagrams and individual HTML pages for
 * each diagram. It also includes functionality to load templates and copy CSS files.
 * 
 * Responsibilities:
 * - Generate an index HTML file with links to all diagrams.
 * - Generate individual HTML pages for each diagram.
 * - Load HTML templates from resources.
 * - Copy CSS files to the output directory.
 * 
 * Dependencies:
 * - {@link DiagramInfo}: Represents metadata about a diagram.
 * - {@link PUML2HTMLDocGenerator}: Provides access to resources for templates and CSS.
 * 
 * Limitations:
 * - Assumes that the provided templates and CSS files exist and are accessible.
 * - Does not validate the correctness of the HTML templates.
 * 
 * Thread Safety:
 * - This class is thread-safe as it does not maintain any mutable state.
 * 
 * Usage Example:
 * {@code
 * Map<String, DiagramInfo> diagramInfoMap = ...;
 * String outputDir = "/path/to/output";
 * String docTitle = "My Documentation";
 * PumlHtmlGenerator.generateIndexFile(diagramInfoMap, outputDir, docTitle, "/templates/index.html");
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class PumlHtmlGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClassDiagramService.class);

    /**
     * Default constructor for PumlHtmlGenerator.
     */
    public PumlHtmlGenerator() {
        // Default constructor
    }

    /**
     * Generates the index HTML file with links to all diagrams.
     * 
     * Responsibilities:
     * - Loads the index template from resources.
     * - Generates sidebar links and main content for all diagrams.
     * - Replaces placeholders in the template with actual content.
     * - Writes the generated HTML to the output directory.
     * 
     * @param diagramInfoMap A map of diagram information, where the key is the diagram ID
     *                       and the value is a {@link DiagramInfo} object containing metadata.
     * @param outputDir      The output directory for the generated HTML file.
     * @param docTitle       The title of the documentation.
     * @param indextemplateFile The path to the index HTML template file.
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static void generateIndexFile(Map<String, DiagramInfo> diagramInfoMap, String outputDir, String docTitle, String indextemplateFile) throws IOException {
        // Load index template
        String indexTemplate = loadTemplate(indextemplateFile);
        
        // Generate sidebar links
        StringBuilder sidebarLinks = new StringBuilder();
        
        // Generate content 
        StringBuilder content = new StringBuilder();
        
        // Sort diagrams by title
        diagramInfoMap.values().stream()
                .sorted((d1, d2) -> d1.getTitle().compareTo(d2.getTitle()))
                .forEach(diagram -> {
                    // Add to sidebar
                    sidebarLinks.append("<li><a href=\"")
                              .append(diagram.getId())
                              .append(".html\">")
                              .append(diagram.getTitle())
                              .append("</a></li>\n");
                    
                    // Add preview to main content
                    content.append("<div class=\"diagram-container\">\n")
                          .append("    <div class=\"diagram-title\">")
                          .append(diagram.getTitle())
                          .append("</div>\n")
                          .append("    <div class=\"diagram-description\">")
                          .append(diagram.getDescription())
                          .append("</div>\n")
                          .append("    <a href=\"")
                          .append(diagram.getId())
                          .append(".html\"><img src=\"")
                          .append(diagram.getImagePath())
                          .append("\" alt=\"")
                          .append(diagram.getTitle())
                          .append("\" class=\"diagram-img\"></a>\n")
                          .append("</div>\n");
                });
        
        // Replace placeholders in template
        String htmlContent = indexTemplate
                .replace("{{title}}", docTitle)
                .replace("{{sidebarLinks}}", sidebarLinks.toString())
                .replace("{{content}}", content.toString())
                .replace("{{generationDate}}", java.time.LocalDate.now().toString());
        
        String outputPath = outputDir + File.separator + "index.html";
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(htmlContent);
        }
    }

    /**
     * Generates an HTML page for a single diagram.
     * 
     * Responsibilities:
     * - Replaces placeholders in the diagram template with actual diagram metadata.
     * - Writes the generated HTML to the output directory.
     * 
     * @param info      The {@link DiagramInfo} object containing metadata about the diagram.
     * @param outputDir The output directory for the generated HTML file.
     * @param template  The HTML template for the diagram page.
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static void generateDiagramPage(DiagramInfo info, String outputDir, String template) throws IOException {
        // Replace template placeholders
        String htmlContent = template
                .replace("{{title}}", info.getTitle())
                .replace("{{description}}", info.getDescription())
                .replace("{{imagePath}}", info.getImagePath())
                .replace("{{imageAlt}}", info.getTitle());
        
        String outputPath = outputDir + File.separator + info.getId() + ".html";
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(htmlContent);
        }
    }

    /**
     * Loads a template file as a String.
     * 
     * Responsibilities:
     * - Reads the content of the specified template file from resources.
     * - Returns the content as a String.
     * 
     * @param templateFilePath The path to the template file to load.
     * @return The content of the template file as a String.
     * @throws IOException If an I/O error occurs while loading the template.
     */
    public static String loadTemplate(String templateFilePath) throws IOException {
        try (InputStream is = PUML2HTMLDocGenerator.class.getResourceAsStream(templateFilePath);
             Scanner scanner = new Scanner(is, "UTF-8")) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new IOException("Error loading template: " + templateFilePath, e);
        }
    }

    /**
     * Copies the CSS file from the resources directory to the specified output directory.
     * 
     * Responsibilities:
     * - Reads the CSS file from the resources directory.
     * - Copies the CSS file to the specified output directory.
     * 
     * @param outputDir          The directory where the CSS file will be copied to.
     * @param cssTemplateFilePath The path to the CSS template file in resources.
     * @param cssDestinationPath The destination path for the copied CSS file.
     * @throws IOException If an I/O error occurs or the CSS template is not found in resources.
     */
    public static void copyCssFile(String outputDir, String cssTemplateFilePath, String cssDestinationPath) throws IOException {
        // Copy CSS from resources to output directory
        try (InputStream cssStream = PUML2HTMLDocGenerator.class.getResourceAsStream(cssTemplateFilePath)) {
            if (cssStream == null) {
                throw new IOException("CSS template not found in resources: " + cssTemplateFilePath);
            }
            Files.copy(cssStream, Paths.get(cssDestinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Error copying CSS file from classpath {} to destination {}: ", cssTemplateFilePath, cssDestinationPath, e.getMessage());
            throw e;
        }
    }
}
