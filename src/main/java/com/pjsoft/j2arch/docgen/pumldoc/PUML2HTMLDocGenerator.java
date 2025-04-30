package com.pjsoft.j2arch.docgen.pumldoc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pjsoft.j2arch.core.util.DirectoryConstants;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.docgen.pumldoc.model.DiagramInfo;
import com.pjsoft.j2arch.docgen.pumldoc.model.DiagramResult;
import com.pjsoft.j2arch.docgen.pumldoc.util.HTMLGenerationContext;
import com.pjsoft.j2arch.docgen.pumldoc.util.PumlHtmlGenerator;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * PUML2HTMLDocGenerator
 * 
 * This class is responsible for generating HTML documentation from PlantUML (.puml) files.
 * It processes the input .puml files, generates diagrams in SVG format, and creates
 * corresponding HTML pages for each diagram. Additionally, it generates an index HTML
 * file to provide an overview of all diagrams.
 * 
 * Responsibilities:
 * - Locate and process .puml files from the specified input directory.
 * - Generate SVG diagrams using PlantUML.
 * - Create HTML pages for each diagram using predefined templates.
 * - Generate an index HTML file to link all diagrams.
 * - Handle parallel processing of multiple .puml files for efficiency.
 * 
 * Dependencies:
 * - {@link HTMLGenerationContext}: Provides configuration details for input/output directories,
 *   templates, and stylesheets.
 * - {@link PumlHtmlGenerator}: Utility class for generating HTML pages and copying stylesheets.
 * - {@link DiagramInfo} and {@link DiagramResult}: Models for storing diagram metadata and results.
 * - PlantUML library: Used for generating diagrams from .puml files.
 * 
 * Limitations:
 * - Assumes that the input directory contains valid .puml files.
 * - Requires valid paths for templates and stylesheets.
 * - Does not handle advanced error recovery for invalid .puml syntax.
 * 
 * Thread Safety:
 * - This class uses a thread pool for parallel processing of .puml files.
 * - Thread safety is ensured for shared resources like logging and file operations.
 * 
 * Usage Example:
 * {@code
 * HTMLGenerationContext context = new HTMLGenerationContext(...);
 * PUML2HTMLDocGenerator generator = new PUML2HTMLDocGenerator();
 * generator.generateHtmlDocumentation("My Documentation", context);
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */

public class PUML2HTMLDocGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PUML2HTMLDocGenerator.class);
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

/**
 * Generates HTML documentation from PlantUML (.puml) files.
 * 
 * Responsibilities:
 * - Copies the CSS file to the output directory.
 * - Locates all .puml files in the specified input directory.
 * - Processes each .puml file to generate diagrams and corresponding HTML pages.
 * - Creates an index HTML file linking all generated diagrams.
 * 
 * @param docTitle The title of the documentation to be displayed in the index file.
 * @param context  The {@link HTMLGenerationContext} containing configuration details such as
 *                 input/output directories, templates, and stylesheets.
 * @throws IOException If an I/O error occurs during file operations.
 * 
 * Limitations:
 * - Assumes that the input directory contains valid .puml files.
 * - Does not handle advanced error recovery for invalid .puml syntax or missing templates.
 */
    public void generateHtmlDocumentation(String docTitle, HTMLGenerationContext context) throws IOException {

        try {

            // createDirectoryIfNotExists(outputDir);
            // createDirectoryIfNotExists(outputDir + "/styles");
            String outputDir = context.getOutputDirectory();
            String inputDir = context.getInputDirectory();
            String diagramTemplateFile = context.getDiagramTemplateFile();
            String indexTemplateFile = context.getIndexTemplateFile();
            String cssTemplateFile = context.getStyleSourceFile();
            String cssOutputFile = context.getStyleOutputFile();
            PumlHtmlGenerator.copyCssFile(outputDir, cssTemplateFile, cssOutputFile);

            List<File> pumlFiles = findPumlFiles(inputDir);
            if (pumlFiles.isEmpty()) {
                logger.info("No .puml files found in directory: " + inputDir);
                return;
            }

            Map<String, DiagramInfo> diagramInfoMap = processFilesAndGenerateDocs(pumlFiles, outputDir,
                    diagramTemplateFile);
            PumlHtmlGenerator.generateIndexFile(diagramInfoMap, outputDir, docTitle, indexTemplateFile);

        } catch (Exception e) {

            logger.error("Error processing PlantUML files:", e.getMessage());

        }
    }

/**
 * Finds all PlantUML (.puml) files in the specified directory.
 * 
 * Responsibilities:
 * - Recursively searches the given directory for files with a `.puml` extension.
 * - Filters out non-regular files and directories.
 * - Collects and returns a list of `.puml` files.
 * 
 * @param directoryPath The path of the directory to search for `.puml` files.
 * @return A list of `.puml` files found in the specified directory.
 * @throws IOException If the directory does not exist, is not accessible, or an I/O error occurs.
 * 
 * Limitations:
 * - Assumes that the directory path provided is valid and accessible.
 * - Does not validate the content of the `.puml` files.
 */
    private List<File> findPumlFiles(String directoryPath) throws IOException {
        List<File> pumlFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory not found: " + directoryPath);
        }
        Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".puml"))
                .forEach(path -> pumlFiles.add(path.toFile()));
        return pumlFiles;
    }

/**
 * Processes the given list of .puml files and generates HTML documentation for each diagram.
 * 
 * Responsibilities:
 * - Uses a thread pool to process multiple .puml files in parallel.
 * - Generates diagrams for each .puml file and creates corresponding HTML pages.
 * - Collects metadata about each diagram and returns it as a map.
 * 
 * @param files        The list of .puml files to process.
 * @param outputDir    The output directory where the generated diagrams and HTML pages will be stored.
 * @param diagTemplate The path to the diagram HTML template.
 * @return A map where the keys are base filenames of the diagrams, and the values are {@link DiagramInfo} objects containing metadata about the diagrams.
 * 
 * Limitations:
 * - Assumes that all input files are valid .puml files.
 * - Does not handle advanced error recovery for file processing failures.
 * 
 * Thread Safety:
 * - Uses a thread pool to ensure efficient parallel processing.
 * - Ensures thread safety for shared resources like logging and file operations.
 */
    private Map<String, DiagramInfo> processFilesAndGenerateDocs(List<File> files, String outputDir,
            String diagTemplate) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        List<Callable<DiagramResult>> tasks = new ArrayList<>();
        for (File file : files) {
            tasks.add(() -> processFile(file, outputDir));
        }
        Map<String, DiagramInfo> diagramInfoMap = new HashMap<>();
        try {
            List<Future<DiagramResult>> results = executor.invokeAll(tasks);
            int successCount = 0;
            int failureCount = 0;
            String diagramTemplate = PumlHtmlGenerator.loadTemplate(diagTemplate);
            for (Future<DiagramResult> future : results) {
                DiagramResult result = future.get();
                if (result.isSuccess()) {
                    successCount++;
                    
                    diagramInfoMap.put(result.getBaseFilename(), result.getDiagramInfo());
                    PumlHtmlGenerator.generateDiagramPage(result.getDiagramInfo(), outputDir, diagramTemplate);
                } else {
                    failureCount++;
                    logger.error("✗ " + result.getFilename() + " - " + result.getMessage());
                }
            }
            logger.info("Processing of puml file complete!");
            logger.info("Successfully processed: " + successCount + " files");
            logger.info("Failed to process: " + failureCount + " files");
        } catch (Exception e) {
            logger.error("Error during parallel processing: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        return diagramInfoMap;
    }

/**
 * Processes a single .puml file and generates the corresponding diagram.
 * 
 * Responsibilities:
 * - Reads the content of the given `.puml` file.
 * - Extracts metadata such as the title and description from the file content.
 * - Generates an SVG diagram using the PlantUML library.
 * - Creates a {@link DiagramInfo} object containing metadata about the diagram.
 * - Returns a {@link DiagramResult} indicating the success or failure of the operation.
 * 
 * @param inputFile The `.puml` file to process.
 * @param outputDir The output directory where the generated diagram will be stored.
 * @return A {@link DiagramResult} containing the result of the diagram generation, including metadata and status.
 * 
 * Limitations:
 * - Assumes that the `.puml` file is valid and readable.
 * - Does not handle advanced error recovery for invalid `.puml` syntax.
 * - Relies on the PlantUML library for diagram generation.
 */
    private DiagramResult processFile(File inputFile, String outputDir) {

        String pumlFilename = inputFile.getName();
        String baseFilename = pumlFilename.substring(0, pumlFilename.lastIndexOf('.'));
        String imageName = baseFilename + ".svg";
        String imageDirName = PathResolver.resolvePath(outputDir , DirectoryConstants.IMAGES_DIR);

        try {
            File imageDir = new File(imageDirName).getAbsoluteFile();

            String pumlContent = new String(Files.readAllBytes(inputFile.toPath()));
            String title = extractTitle(pumlContent, baseFilename);
            String description = extractDescription(pumlContent, "");

            SourceFileReader reader = new SourceFileReader(inputFile, imageDir, new FileFormatOption(FileFormat.SVG));
            List<?> diagrams = reader.getGeneratedImages();

            String relativeImagePath = PathResolver.resolvePath(DirectoryConstants.IMAGES_DIR, imageName);
            DiagramInfo diagramInfo = new DiagramInfo(baseFilename, title, description, relativeImagePath);
            return new DiagramResult(pumlFilename, baseFilename, true, diagrams.size() + " diagram(s) generated",
                    diagramInfo);
        } catch (Exception e) {
            return new DiagramResult(pumlFilename, baseFilename, false, e.getMessage(), null);
        }
    }

    /**
     * Creates a directory if it does not exist.
     *
     * @param directoryPath The directory path to create
     * @throws IOException If an I/O error occurs
     */
    private synchronized void createDirectoryIfNotExists(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directoryPath);
            }
        }
    }

    /**
     * Extracts the title from the PlantUML content.
     *
     * @param pumlContent  The PlantUML content
     * @param defaultTitle The default title if none is found
     * @return The extracted title
     */
    private String extractTitle(String pumlContent, String defaultTitle) {
        Pattern titlePattern = Pattern.compile("(?:title|Title|TITLE)\\s+(.*?)(?:\\n|$)");
        Matcher titleMatcher = titlePattern.matcher(pumlContent);
        if (titleMatcher.find()) {
            return titleMatcher.group(1).trim();
        }
        Pattern startPattern = Pattern.compile("@startuml\\s+(.*?)(?:\\n|$)");
        Matcher startMatcher = startPattern.matcher(pumlContent);
        if (startMatcher.find() && !startMatcher.group(1).trim().isEmpty()) {
            return startMatcher.group(1).trim();
        }
        return defaultTitle.replace("_", " ").replace("-", " ");
    }

    /**
     * Extracts the description from the PlantUML content.
     *
     * @param pumlContent        The PlantUML content
     * @param defaultDescription The default description if none is found
     * @return The extracted description
     */
    private String extractDescription(String pumlContent, String defaultDescription) {
        Pattern descPattern = Pattern.compile(
                "(?:note|Note|NOTE)\\s+(?:left|right|top|bottom)\\s*:\\s*(.*?)(?:end note|$)",
                Pattern.DOTALL);
        Matcher descMatcher = descPattern.matcher(pumlContent);
        if (descMatcher.find()) {
            return descMatcher.group(1).trim().replaceAll("\\s+", " ");
        }
        return defaultDescription;
    }

}