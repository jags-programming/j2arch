package com.pjsoft.j2arch.docgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pjsoft.j2arch.docgen.model.DiagramInfo;
import com.pjsoft.j2arch.docgen.model.DiagramResult;
import com.pjsoft.j2arch.docgen.utils.HtmlGenerator;


import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * PlantUMLDocGenerator is the main class for generating HTML documentation from PlantUML files.
 * It processes .puml files, generates diagrams, and creates HTML documentation.
 */
public class Image2DocGenerator {

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static final String DIAGRAM_TEMPLATE = "diagram.html";

    /**
     * Main method to run the PlantUMLDocGenerator.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Image2DocGenerator generator = new Image2DocGenerator();
        generator.run();
    }

    /**
     * Runs the PlantUMLDocGenerator.
     * It prompts the user for input and output directories, processes .puml files, and generates HTML documentation.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        String inputDir = getInputDirectory(scanner);
        String outputDir = getOutputDirectory(scanner);
        String docTitle = getDocumentationTitle(scanner);

        System.out.println("\nPlantUML Documentation Generator");
        System.out.println("================================");
        System.out.println("Input directory: " + inputDir);
        System.out.println("Output directory: " + outputDir);
        System.out.println("Documentation title: " + docTitle);
        System.out.println("Using " + MAX_THREADS + " threads for processing");

        try {
            createDirectoryIfNotExists(outputDir);
            HtmlGenerator.copyCssFile(outputDir);

            List<File> pumlFiles = findPumlFiles(inputDir);
            if (pumlFiles.isEmpty()) {
                System.out.println("No .puml files found in directory: " + inputDir);
                return;
            }

            System.out.println("Found " + pumlFiles.size() + " PlantUML files to process");

            Map<String, DiagramInfo> diagramInfoMap = processFilesAndGenerateDocs(pumlFiles, outputDir);
            HtmlGenerator.generateIndexFile(diagramInfoMap, outputDir, docTitle);

            System.out.println("\nDocumentation generation complete!");
            System.out.println("HTML documentation available at: " + outputDir + File.separator + "index.html");

        } catch (Exception e) {
            System.err.println("Error processing PlantUML files: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Prompts the user for the input directory containing .puml files.
     * 
     * @param scanner Scanner object for reading user input
     * @return The input directory path
     */
    private String getInputDirectory(Scanner scanner) {
        System.out.print("Enter the absolute directory path containing PUML files [default: puml]: ");
        String inputDir = scanner.nextLine().trim();
        if (inputDir.isEmpty()) {
            inputDir = Paths.get("").toAbsolutePath().toString() + File.separator + "puml";
        }
        return inputDir;
    }

    /**
     * Prompts the user for the output directory for generated documentation.
     * 
     * @param scanner Scanner object for reading user input
     * @return The output directory path
     */
    private String getOutputDirectory(Scanner scanner) {
        System.out.print("Enter the absolute output directory path for documentation [default: docs]: ");
        String outputDir = scanner.nextLine().trim();
        if (outputDir.isEmpty()) {
            outputDir = Paths.get("").toAbsolutePath().toString() + File.separator + "docs";
        } else {
            Path outputPath = Paths.get(outputDir);
            if (!outputPath.isAbsolute()) {
                outputDir = Paths.get("").toAbsolutePath().toString() + File.separator + outputDir;
            }
        }
        return outputDir;
    }

    /**
     * Prompts the user for the documentation title.
     * 
     * @param scanner Scanner object for reading user input
     * @return The documentation title
     */
    private String getDocumentationTitle(Scanner scanner) {
        System.out.print("Enter the documentation title [PlantUML Documentation]: ");
        String docTitle = scanner.nextLine().trim();
        if (docTitle.isEmpty()) {
            docTitle = "PlantUML Documentation";
        }
        return docTitle;
    }

    /**
     * Finds all .puml files in the specified directory.
     * 
     * @param directoryPath The directory path to search for .puml files
     * @return A list of .puml files
     * @throws IOException If an I/O error occurs
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
     * Processes the .puml files and generates HTML documentation.
     * 
     * @param files The list of .puml files to process
     * @param outputDir The output directory for generated documentation
     * @return A map of diagram information
     */
    private Map<String, DiagramInfo> processFilesAndGenerateDocs(List<File> files, String outputDir) {
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
            String diagramTemplate = HtmlGenerator.loadTemplate(DIAGRAM_TEMPLATE);
            for (Future<DiagramResult> future : results) {
                DiagramResult result = future.get();
                if (result.isSuccess()) {
                    successCount++;
                    System.out.println("✓ " + result.getFilename() + " - " + result.getMessage());
                    diagramInfoMap.put(result.getBaseFilename(), result.getDiagramInfo());
                    HtmlGenerator.generateDiagramPage(result.getDiagramInfo(), outputDir, diagramTemplate);
                } else {
                    failureCount++;
                    System.err.println("✗ " + result.getFilename() + " - " + result.getMessage());
                }
            }
            System.out.println("\nProcessing complete!");
            System.out.println("Successfully processed: " + successCount + " files");
            System.out.println("Failed to process: " + failureCount + " files");
        } catch (Exception e) {
            System.err.println("Error during parallel processing: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        return diagramInfoMap;
    }

    /**
     * Processes a single .puml file and generates the corresponding diagram.
     * 
     * @param inputFile The .puml file to process
     * @param outputDir The output directory for generated documentation
     * @return The result of the diagram generation
     */
    private DiagramResult processFile(File inputFile, String outputDir) {
        String filename = inputFile.getName();
        String baseFilename = filename.substring(0, filename.lastIndexOf('.'));
        try {
            String imagesDir = outputDir + File.separator + "images";
            createDirectoryIfNotExists(imagesDir);
            File outputFile = new File(imagesDir + File.separator + baseFilename + ".svg");
            String pumlContent = new String(Files.readAllBytes(inputFile.toPath()));
            String title = extractTitle(pumlContent, baseFilename);
            String description = extractDescription(pumlContent, "");
            SourceFileReader reader = new SourceFileReader(inputFile, outputFile.getParentFile(),
                    new FileFormatOption(FileFormat.SVG));
            List<?> diagrams = reader.getGeneratedImages();
            String relativeImagePath = "images/" + baseFilename + ".svg";
            DiagramInfo diagramInfo = new DiagramInfo(baseFilename, title, description, relativeImagePath);
            return new DiagramResult(filename, baseFilename, true, diagrams.size() + " diagram(s) generated", diagramInfo);
        } catch (Exception e) {
            return new DiagramResult(filename, baseFilename, false, e.getMessage(), null);
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
     * @param pumlContent The PlantUML content
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
     * @param pumlContent The PlantUML content
     * @param defaultDescription The default description if none is found
     * @return The extracted description
     */
    private String extractDescription(String pumlContent, String defaultDescription) {
        Pattern descPattern = Pattern.compile("(?:note|Note|NOTE)\\s+(?:left|right|top|bottom)\\s*:\\s*(.*?)(?:end note|$)",
                Pattern.DOTALL);
        Matcher descMatcher = descPattern.matcher(pumlContent);
        if (descMatcher.find()) {
            return descMatcher.group(1).trim().replaceAll("\\s+", " ");
        }
        return defaultDescription;
    }
}