package com.pjsoft.j2arch.cli;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import com.pjsoft.j2arch.config.ConfigurationLoader;
import com.pjsoft.j2arch.config.ContextFactory;
import com.pjsoft.j2arch.config.DefaultContextFactory;
import com.pjsoft.j2arch.core.util.ConfigurationValidator;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ResourcePaths;
import com.pjsoft.j2arch.core.util.ProgressTracker.UseCase;
import com.pjsoft.j2arch.uml.UMLDiagramGenerator;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * Command-Line Interface (CLI) Application for UML Diagram Generation.
 * 
 * This class serves as the entry point for the UML Diagram Generator application.
 * It provides a command-line interface for users to configure settings, generate
 * UML diagrams, and handle errors gracefully.
 * 
 * Features:
 * - Displays a user-friendly CLI for configuration and diagram generation.
 * - Supports three configuration modes:
 *   1. Default settings
 *   2. File-based settings (loaded from a properties file)
 *   3. Custom inputs (interactive user input)
 * - Validates configuration settings before proceeding.
 * - Invokes the UML diagram generation process.
 * - Handles application lifecycle events, such as shutdown hooks and error handling.
 * 
 * Refactored Design:
 * - Modularized configuration handling using `ContextFactory`.
 * - Improved error handling and logging.
 * - Added support for shutdown hooks to handle interruptions (e.g., Ctrl+C).
 * - Enhanced user prompts and feedback for better usability.
 * 
 * @author PJSoft
 * @version 2.0
 * @since 1.0
 */
public class CLIApplication {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CLIApplication.class);
    private static boolean ctrlPressed = false;

    /**
     * Main entry point for the CLI application.
     * 
     * Responsibilities:
     * - Initializes the application.
     * - Loads configuration settings.
     * - Handles user input for configuration choices.
     * - Invokes the UML diagram generation process.
     * - Manages application shutdown gracefully.
     * 
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        addShutdownHook(); // Add a shutdown hook to handle Ctrl+C (SIGINT)

        try {
            printBanner(); // Display ASCII banner
            printApplicationTitle(); // Display application title

            // Load properties using ConfigurationLoader
            Properties properties = ConfigurationLoader.loadConfiguration();

            // Display options and collect user input
            printOptionsMessage(properties);
            String configChoice = collectConfigurationChoice();

            // Handle user choice and create contexts
            ContextFactory factory = new DefaultContextFactory(properties);
            UMLGenerationContext initialContext = factory.createUMLContext();
            UMLGenerationContext umlContext = handleConfigurationChoice(configChoice, initialContext);

            // Create a CLI-compatible ProgressTracker
        ProgressTracker progressTracker = new ProgressTracker(UseCase.UML_DIAGRAM_GENERATION); // No GUI components

            // Generate UML diagrams
            UMLDiagramGenerator diagramGenerator = new UMLDiagramGenerator();
            diagramGenerator.generateDiagrams(umlContext, progressTracker);

            printSuccessMessage(); // Display success message
        } catch (java.util.NoSuchElementException e) {
            ctrlPressed = true;
            logger.info("Ctrl+C pressed"); // Suppress error caused by Ctrl+C
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
            logger.error("An error occurred while generating UML diagrams: ", e);
        }
    }

    /**
     * Collects the user's configuration choice from the command line.
     * 
     * @return The user's choice as a string ("1", "2", or "3").
     */
    private static String collectConfigurationChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(Styler.bold("Enter your choice (default 1): "));
        String choice = scanner.nextLine().trim();
        return choice.isEmpty() ? "1" : choice;
    }

    /**
     * Handles the user's configuration choice and creates the appropriate UMLGenerationContext.
     * 
     * @param configChoice The user's choice ("1", "2", or "3").
     * @param initialContext The initial context with default settings.
     * @return The configured UMLGenerationContext.
     * @throws IOException If an error occurs while loading file-based settings.
     */
    private static UMLGenerationContext handleConfigurationChoice(String configChoice, UMLGenerationContext initialContext) throws IOException {
        UMLGenerationContext context = null;
        switch (configChoice) {
            case "1":
                System.out.println(Styler.green("Using default settings..."));
                context = initialContext; // Default settings
                break;
            case "2":

            String customPropertiesFileName=null; // Once application will have file chooser to choose it will be populated
            context = getContextFromFile(customPropertiesFileName);
                break;
            case "3":
                System.out.println(Styler.green("Entering custom configuration..."));
                context = collectCustomUMLContext(initialContext); // Custom inputs
                break;
            default:
                System.out.println(Styler.red("Invalid choice. Exiting application."));
                logger.warn("Invalid choice entered: " + configChoice);
                System.exit(1);
        }

        // Validate the context before returning
        ConfigurationValidator.validateContext(context);
        return context;
    }

    private static UMLGenerationContext getContextFromFile  (String customPropertiesFileName) throws IOException {
        // Load properties using ConfigurationLoader
        Properties fileProperties = ConfigurationLoader.loadProperties(customPropertiesFileName);

     // Use DefaultContextFactory to create UMLGenerationContext
     DefaultContextFactory contextFactory = new DefaultContextFactory(fileProperties);
     return contextFactory.createUMLContext();
    }

    /**
     * Collects custom configuration inputs interactively from the user.
     * 
     * @param initialContext The initial context with default settings.
     * @return A new UMLGenerationContext based on user inputs.
     */
    private static UMLGenerationContext collectCustomUMLContext(UMLGenerationContext initialContext) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter input directory (default: ./input): ");
        String inputDirectory = scanner.nextLine().trim();
        inputDirectory = inputDirectory.isEmpty() ? initialContext.getInputDirectory() : inputDirectory;

        System.out.print("Enter output directory (default: ./umldoc): ");
        String outputDirectory = scanner.nextLine().trim();
        outputDirectory = outputDirectory.isEmpty() ? initialContext.getOutputDirectory() : outputDirectory;

        System.out.print("Enter diagram types (comma-separated, e.g., class,sequence): ");
        String diagramTypes = scanner.nextLine().trim();
        diagramTypes = diagramTypes.isEmpty() ? initialContext.getDiagramTypes() : diagramTypes;

        System.out.print("Enter package to include (default: all): ");
        String includePackage = scanner.nextLine().trim();
        includePackage = includePackage.isEmpty() ? initialContext.getIncludePackage() : includePackage;

        String pumlPath = initialContext.getPumlPath();
        String imagesOutputPath = initialContext.getImagesOutputDirectory();
        String unifiedClassDiagram = initialContext.getUnifiedClassDiagram();
        String libsDirPath = initialContext.getLibsDirPath();

        // Create and return a new UMLGenerationContext
        return new UMLGenerationContext(
            inputDirectory,
            outputDirectory,
            diagramTypes,
            includePackage,
            pumlPath,
            imagesOutputPath,
            unifiedClassDiagram,
            libsDirPath
        );
    }

    /**
     * Prints the available configuration options to the console.
     * 
     * @param properties The loaded properties to display default settings.
     */
    private static void printOptionsMessage(Properties properties) {
        String defaultSettingsDisplay = properties.entrySet().stream()
            .filter(entry -> 
                "input.uml.directory".equals(entry.getKey()) || 
                "output.uml.directory".equals(entry.getKey()))
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .reduce((key1, key2) -> key1 + ", " + key2)
            .orElse("No relevant default settings available");

        String[] lines = {
            "Welcome to the UML Diagram Generator!",
            "This tool helps you generate UML diagrams from Java source code.",
            "This application requires some inputs for configuration settings.",
            "How do you want to proceed?",
            "1. Use default settings (pre-configured values for input, output, and diagram types).",
            "   [e.g.: " + defaultSettingsDisplay + "]",
            "2. Load settings from a configuration file (relative path: "+ ResourcePaths.CUSTOM_CONFIG_FILE+ ")",
            "3. Enter custom inputs interactively (you will be prompted for each setting)."
        };

        for (String line : lines) {
            System.out.println(Styler.green(line));
        }
    }

    /**
     * Adds a shutdown hook to handle application termination gracefully.
     */
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ctrlPressed) {
                System.out.println(Styler.yellow("\nApplication interrupted. Exiting gracefully..."));
                logger.info("Application interrupted by user (Ctrl+C).");
            } else {
                logger.info("Application shutting down.");
            }
        }));
    }

    /**
     * Prints the ASCII banner for the application.
     */
    private static void printBanner() {
        System.out.println(Styler.blue("""
                ██████╗     ██╗    ██╗   ██╗███╗   ███╗██╗
                ██╔══██╗    ██║    ██║   ██║████╗ ████║██║
                ██████╔╝    ██║    ██║   ██║██╔████╔██║██║
                ██╔═══╝██   ██║    ██║   ██║██║╚██╔╝██║██║
                ██║    ╚█████╔╝    ╚██████╔╝██║ ╚═╝ ██║███████╗
                ╚═╝     ╚════╝      ╚═════╝ ╚═╝     ╚═╝╚══════╝
                """));
    }

    /**
     * Prints the application title in stylized text.
     */
    private static void printApplicationTitle() {
        System.out.println(Styler.bold(""));
        System.out.println(Styler.blue("""
                 ╦┌─┐┬  ┬┌─┐  ╔╦╗┌─┐  ╦ ╦╔╦╗╦    ╔═╗┌─┐┌┐┌┬  ┬┌─┐┬─┐┌┬┐┌─┐┬─┐
                 ║├─┤└┐┌┘├─┤   ║ │ │  ║ ║║║║║    ║  │ ││││└┐┌┘├┤ ├┬┘ │ ├┤ ├┬┘
                ╚╝┴ ┴ └┘ ┴ ┴   ╩ └─┘  ╚═╝╩ ╩╩═╝  ╚═╝└─┘┘└┘ └┘ └─┘┴└─ ┴ └─┘┴└─
                        """));
        System.out.println(Styler.bold(""));
    }

    /**
     * Prints a success message after UML diagrams are generated.
     */
    private static void printSuccessMessage() {
        System.out.println(Styler.green("UML diagrams generated successfully!"));
    }

    /**
     * Prints an error message to the console.
     * 
     * @param message The error message to display.
     */
    private static void printErrorMessage(String message) {
        System.err.println(Styler.red("Error: " + message));
    }
}
