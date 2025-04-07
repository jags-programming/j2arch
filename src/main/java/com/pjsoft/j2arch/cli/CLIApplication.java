package com.pjsoft.j2arch.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.pjsoft.j2arch.uml.UMLDiagramGenerator;
import com.pjsoft.j2arch.uml.config.ConfigurationManager;

/**
 * Command-Line Interface (CLI) Application for UML Diagram Generation.
 * 
 * This class serves as the entry point for the UML Diagram Generator application.
 * It provides a command-line interface for users to configure settings, generate
 * UML diagrams, and handle errors gracefully.
 * 
 * Responsibilities:
 * - Displays a user-friendly CLI for configuration and diagram generation.
 * - Handles user input for configuration choices (default, file-based, or custom).
 * - Invokes the UML diagram generation process.
 * - Manages application lifecycle events, such as shutdown hooks and error handling.
 * 
 * Usage Example:
 * {@code
 * java -jar UMLDiagramGenerator.jar
 * }
 * 
 * Dependencies:
 * - {@link ConfigurationManager}
 * - {@link UMLDiagramGenerator}
 * - {@link Styler}
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on mutable state and user input.
 * 
 * Limitations:
 * - Assumes that the user provides valid input for configuration settings.
 * - Requires a valid configuration file if file-based settings are chosen.
 * 
 * @author PJSoft
 * @version 1.1
 * @since 1.0
 */
public class CLIApplication {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CLIApplication.class);
    private static boolean ctrlPressed = false;

    /**
     * The main entry point for the application.
     * 
     * Responsibilities:
     * - Initializes the application and displays the CLI.
     * - Handles user input for configuration and invokes the UML diagram generation process.
     * - Manages application shutdown and error handling.
     * 
     * @param args command-line arguments (not used in this application).
     * @since 1.0
     */
    public static void main(String[] args) {
        addShutdownHook(); // Add a shutdown hook to handle Ctrl+C (SIGINT)

        try {
            printBanner(); // Display ASCII banner
            printApplicationTitle();
            ConfigurationManager configManager = ConfigurationManager.getInstance();
            printOptionsMessage(configManager);
            String configChoice = collectConfigurationChoice();
            handleConfigurationChoice(configChoice, configManager);
            configManager.validateConfig();
            UMLDiagramGenerator diagramGenerator = new UMLDiagramGenerator(configManager);
            diagramGenerator.generateDiagrams();
            printSuccessMessage();
        } catch (java.util.NoSuchElementException e) {
            ctrlPressed = true;
            logger.info("Ctrl+C pressed"); // Suppress error caused by Ctrl+C
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
            logger.error("An error occurred while generating UML diagrams: ", e);
        }
    }

    /**
     * Collects the user's configuration choice from the CLI.
     * 
     * Responsibilities:
     * - Prompts the user to select a configuration option.
     * - Returns the user's choice or a default value if no input is provided.
     * 
     * @return the user's configuration choice.
     * @since 1.0
     */
    private static String collectConfigurationChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(Styler.bold("Enter your choice (default 1): "));
        String choice = scanner.nextLine().trim();
        if (choice.isEmpty()) {
            choice = "1";
        }
        return choice;
    }

    /**
     * Handles the user's configuration choice and loads the appropriate settings.
     * 
     * Responsibilities:
     * - Loads default settings, file-based settings, or custom inputs based on the user's choice.
     * 
     * Preconditions:
     * - The user's choice must be valid (1, 2, or 3).
     * 
     * Postconditions:
     * - The configuration manager is updated with the selected settings.
     * 
     * @param configChoice the user's configuration choice.
     * @param configManager the configuration manager to update.
     * @throws IOException if file-based settings cannot be loaded.
     * @since 1.0
     */
     static void handleConfigurationChoice(String configChoice, ConfigurationManager configManager)
            throws IOException {
        switch (configChoice) {
            case "1":
                System.out.println(Styler.green("Using default settings..."));
                configManager.loadDefaultConfig();
                break;
            case "2":
                try {
                    System.out.println(Styler.green("Loading settings from config/application.properties..."));
                    configManager.loadFileConfig("config/application.properties");
                    logger.info("Configuration loaded from file");
                } catch (IOException e) {
                    logger.error("Error loading configuration file", e);
                    throw new IOException("Unable to load configuration from file. " + e.getMessage());
                }
                break;
            case "3":
                try {
                    Map<String, String> customInputs = collectCustomInputs();
                    configManager.loadCustomConfig(customInputs);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unable to load custom configuration. " + e.getMessage());
                }
                break;
            default:
                System.out.println(Styler.red("Invalid choice. Exiting application."));
                logger.warn("Invalid choice entered: " + configChoice);
                System.exit(1);
        }
    }

    /**
     * Collects custom configuration inputs from the user.
     * 
     * Responsibilities:
     * - Prompts the user for input directory, output directory, and diagram types.
     * 
     * @return a map containing the custom configuration inputs.
     * @since 1.0
     */
    private static Map<String, String> collectCustomInputs() {
        printMessage("Custom Configuration Inputs: ");
        Map<String, String> customInputs = new HashMap<>();
        customInputs.put("input.directory", promptForInput("Enter input directory (default: ./input):", "input.directory", "./input"));
        customInputs.put("output.directory", promptForInput("Enter output directory (default: ./output):", "output.directory", "./output"));
        customInputs.put("diagram.types", promptForInput("Enter diagram types (e.g., class, sequence):", "diagram.types", "class,sequence"));
        customInputs.put("include.package", promptForInput("Enter package to include (default: all):", "include.package", ""));
        return customInputs;
    }

    /**
     * Adds a shutdown hook to handle application termination events (e.g., Ctrl+C).
     * 
     * Responsibilities:
     * - Logs a message and displays a user-friendly message when the application is interrupted.
     * 
     * @since 1.0
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
 * Prompts the user for input with an optional default value.
 * 
 * Responsibilities:
 * - Displays a prompt message and collects user input.
 * - Returns the user's input or the default value if no input is provided.
 * - Handles special cases like "include.package" where an empty input is valid.
 * 
 * @param prompt the prompt message to display.
 * @param key the key identifying the configuration property (e.g., "include.package").
 * @param defaultValue the default value to use if no input is provided.
 * @return the user's input or the default value.
 * @since 1.0
 */
private static String promptForInput(String prompt, String key, String defaultValue) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
        System.out.print(Styler.bold(prompt + " "));
        String input = scanner.nextLine().trim();

        // Special case for "include.package" to allow empty input
        if ("include.package".equals(key)) {
            return input.isEmpty() ? "" : input; // Return empty string if no input is provided
        }

        if (!input.isEmpty()) {
            return input;
        } else if (defaultValue != null && !defaultValue.isEmpty()) {
            return defaultValue;
        } else {
            System.out.println(Styler.red("This field is required. Please enter a value."));
        }
    }
}

    /**
     * Prints an ASCII banner for the application.
     * 
     * Responsibilities:
     * - Displays a visually appealing banner at the start of the application.
     * 
     * @since 1.0
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
     * Prints the application title in a styled format.
     * 
     * Responsibilities:
     * - Displays the application title in a visually appealing format.
     * 
     * @since 1.0
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
     * 
     * Responsibilities:
     * - Displays a success message to the user.
     * 
     * @since 1.0
     */
    private static void printSuccessMessage() {
        printMessage("Success");
        System.out.println(Styler.green("UML diagrams generated successfully!"));
        System.out.println(Styler.green("Check the output directory for the generated diagrams."));
    }

    /**
     * Prints an error message when an exception occurs.
     * 
     * Responsibilities:
     * - Displays an error message to the user.
     * 
     * @param message the error message to display.
     * @since 1.0
     */
    private static void printErrorMessage(String message) {
        printMessage("Error");
        System.err.println(Styler.red("An error occurred: " + message));
    }

    /**
     * Prints the configuration options message to the user.
     * 
     * Responsibilities:
     * - Displays the available configuration options in a styled format.
     * 
     * @param config the configuration manager containing default settings.
     * @since 1.0
     */
    private static void printOptionsMessage(ConfigurationManager config) {
        Map<String, String> defaultSettings = config.getDefaultSettings();
        String defaultSettingsDisplay = defaultSettings.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((key1, key2) -> key1 + ", " + key2)
                .orElse("No default settings available");

        String[] lines = {
                "Welcome to the UML Diagram Generator!",
                "This tool helps you generate UML diagrams from Java source code.",
                "This application requires some inputs for configuration settings.",
                "How do you want to proceed?",
                "1. Use default settings (pre-configured values for input, output, and diagram types).",
                " [e.g.:" + defaultSettingsDisplay + "]",
                "2. Load settings from a configuration file (config/application.properties).",
                "3. Enter custom inputs interactively (you will be prompted for each setting)."
        };

        for (String line : lines) {
            String styledLine = Styler.green(line);
            System.out.println(styledLine);
        }
    }

    /**
     * Prints a styled message to the console.
     * 
     * Responsibilities:
     * - Displays a message in a visually appealing format.
     * 
     * @param message the message to display.
     * @since 1.0
     */
    private static void printMessage(String message) {
        System.out.println(Styler.bold(""));
        System.out.println(Styler.blue("  " + message));
        System.out.println(Styler.bold(""));
    }
}
