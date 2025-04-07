package com.pjsoft.j2arch.docgen.utils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Scanner;

import com.pjsoft.j2arch.docgen.Image2DocGenerator;
import com.pjsoft.j2arch.docgen.model.DiagramInfo;

//import com.pjsoft.j2arch.uml.html.Image2DocGenerator;
//import com.pjsoft.j2arch.uml.html.model.DiagramInfo;

/**
 * HtmlGenerator is a utility class for generating HTML files for PlantUML diagrams.
 * It provides methods to generate an index file and individual diagram pages.
 */
public class HtmlGenerator {

    private static final String INDEX_TEMPLATE = "index.html";
    private static final String TEMPLATE_DIR = "/templates/";
    private static final String CSS_FILE = "styles.css";

    /**
     * Default constructor for HtmlGenerator.
     */
    public HtmlGenerator() {
        // Default constructor
    }

    /**
     * Generates the index HTML file with links to all diagrams.
     *
     * @param diagramInfoMap A map of diagram information
     * @param outputDir The output directory for the generated HTML file
     * @param docTitle The title of the documentation
     * @throws IOException If an I/O error occurs
     */
    public static void generateIndexFile(Map<String, DiagramInfo> diagramInfoMap, String outputDir, String docTitle) throws IOException {
        // Load index template
        String indexTemplate = loadTemplate(INDEX_TEMPLATE);
        
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
     * @param info The information about the diagram
     * @param outputDir The output directory for the generated HTML file
     * @param template The HTML template for the diagram page
     * @throws IOException If an I/O error occurs
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
     * @param templateName The name of the template file to load
     * @return The content of the template file as a String
     * @throws IOException If an I/O error occurs while loading the template
     */
    public static String loadTemplate(String templateName) throws IOException {
        try (InputStream is = Image2DocGenerator.class.getResourceAsStream(TEMPLATE_DIR + templateName);
             Scanner scanner = new Scanner(is, "UTF-8")) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new IOException("Error loading template: " + templateName, e);
        }
    }

    /**
     * Copies the CSS file from the resources directory to the specified output directory.
     *
     * @param outputDir The directory where the CSS file will be copied to
     * @throws IOException If an I/O error occurs or the CSS template is not found in the resources
     */
    public static void copyCssFile(String outputDir) throws IOException {
        String cssOutputPath = outputDir + File.separator + CSS_FILE;
        
        // Copy CSS from resources to output directory
        try (InputStream cssStream = Image2DocGenerator.class.getResourceAsStream(TEMPLATE_DIR + CSS_FILE)) {
            if (cssStream == null) {
                throw new IOException("CSS template not found in resources");
            }
            Files.copy(cssStream, Paths.get(cssOutputPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
