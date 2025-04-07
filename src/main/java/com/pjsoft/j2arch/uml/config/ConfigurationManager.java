package com.pjsoft.j2arch.uml.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Manager (Singleton)
 * 
 * Manages application configurations by loading, validating, and providing access to
 * configuration properties. This class supports loading configurations from default
 * settings, files, or custom inputs.
 * 
 * Responsibilities:
 * - Loads default, file-based, or custom configurations.
 * - Provides access to configuration properties.
 * - Validates configuration properties to ensure correctness.
 * 
 * Supported Configuration Properties:
 * - input.directory: The directory containing the input `.java` files.
 * - output.directory: The directory where the generated UML diagrams will be saved.
 * - diagram.types: The types of UML diagrams to generate (e.g., class, sequence).
 * - include.package: The package to include in the UML diagrams (e.g., `com.example`).
 *   If empty, all packages will be included.
 * 
 * Usage Example:
 * {@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * config.loadDefaultConfig();
 * String inputDir = config.getProperty("input.directory");
 * String includePackage = config.getProperty("include.package");
 * System.out.println("Input Directory: " + inputDir);
 * System.out.println("Include Package: " + includePackage);
 * }
 * 
 * Thread Safety:
 * - This class is thread-safe as it uses synchronized methods for instance creation.
 * 
 * Limitations:
 * - Assumes that configuration keys and values are valid strings.
 * - Requires valid directory paths for input and output configurations.
 * 
 * @author PJSoft
 * @version 1.2
 * @since 1.0
 */
public class ConfigurationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private Map<String, String> settings;

    /**
     * Private constructor to enforce the Singleton pattern.
     * 
     * Initializes the settings map.
     * 
     * @since 1.0
     */
    private ConfigurationManager() {
        settings = new HashMap<>();
    }

    /**
     * Returns the singleton instance of the ConfigurationManager.
     * 
     * @return the singleton instance of the ConfigurationManager.
     * @since 1.0
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Loads default configuration settings.
     * 
     * Responsibilities:
     * - Populates the settings map with default values.
     * 
     * Postconditions:
     * - Default settings are loaded into the configuration.
     * 
     * @since 1.0
     */
    public void loadDefaultConfig() {
        logger.info("Loading default configurations...");
        settings.putAll(getDefaultSettings());
        logger.info("Default settings loaded successfully.");
    }

    /**
     * Loads configuration settings from a file.
     * 
     * Responsibilities:
     * - Reads key-value pairs from the specified file and populates the settings map.
     * 
     * Preconditions:
     * - The file must exist and be readable.
     * 
     * Postconditions:
     * - Configuration settings from the file are loaded into the configuration.
     * 
     * @param filePath the path to the configuration file.
     * @throws IOException if the file cannot be read.
     * @since 1.0
     */
    public void loadFileConfig(String filePath) throws IOException {
        logger.info("Loading configuration from file: {}", filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    settings.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading configuration file: {}", filePath, e);
            throw new IOException("Unable to read file: " + filePath, e);
        }

        logger.info("Configuration loaded successfully.");
    }

    /**
     * Loads custom configuration settings from a map.
     * 
     * Responsibilities:
     * - Adds or updates configuration settings from the provided map.
     * 
     * Preconditions:
     * - The custom inputs map must not be null or empty.
     * 
     * Postconditions:
     * - Custom configuration settings are loaded into the configuration.
     * 
     * @param customInputs a map containing custom configuration settings.
     * @throws IllegalArgumentException if the custom inputs map is null or empty.
     * @since 1.0
     */
    public void loadCustomConfig(Map<String, String> customInputs) {
        if (customInputs == null || customInputs.isEmpty()) {
            logger.error("Custom inputs are null or empty.");
            throw new IllegalArgumentException("Custom inputs cannot be null or empty.");
        }
        for (Map.Entry<String, String> entry : customInputs.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
        logger.info("Custom configuration loaded successfully.");
    }

    /**
     * Clears all configuration settings.
     * 
     * Postconditions:
     * - The settings map is cleared.
     * 
     * @since 1.0
     */
    public void clearSettings() {
        settings.clear();
    }

    /**
     * Gets a configuration value by key.
     * 
     * @param key the configuration key.
     * @return the configuration value, or an empty string if the key is not found.
     * @since 1.0
     */
    public String getProperty(String key) {
        return settings.getOrDefault(key, "");
    }

    /**
     * Gets all configuration properties as an unmodifiable map.
     * 
     * @return a map containing all configuration properties.
     * @since 1.0
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(settings);
    }

    /**
     * Returns the default configuration settings as a map.
     * 
     * Default Settings:
     * - input.directory: "./input"
     * - output.directory: "./output"
     * - diagram.types: "class,sequence"
     * - include.package: "" (includes all packages)
     * 
     * @return a map containing the default configuration settings.
     * @since 1.0
     */
    public Map<String, String> getDefaultSettings() {
        Map<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("input.directory", "./input");
        defaultSettings.put("output.directory", "./output");
        defaultSettings.put("diagram.types", "class,sequence");
        defaultSettings.put("include.package", ""); // Default value for include.package
        return defaultSettings;
    }

    /**
     * Sets a configuration property.
     * 
     * Responsibilities:
     * - Validates the key and value before adding it to the settings map.
     * 
     * Preconditions:
     * - The key must be a valid configuration key.
     * - The value must be valid for the specified key.
     * 
     * Supported Keys:
     * - input.directory: Must be a valid directory path.
     * - output.directory: Must be a valid directory path.
     * - diagram.types: Must be a comma-separated list of valid diagram types (e.g., class, sequence).
     * - include.package: Must be a valid Java package name or empty (to include all packages).
     * 
     * Postconditions:
     * - The configuration property is added or updated in the settings map.
     * 
     * @param key the configuration key.
     * @param value the configuration value.
     * @throws IllegalArgumentException if the key or value is invalid.
     * @since 1.0
     */
    public void setProperty(String key, String value) throws IllegalArgumentException {
        switch (key) {
            case "input.directory":
            case "output.directory":
                validateDirectory(value, key);
                break;
            case "diagram.types":
                validateDiagramTypes(value);
                break;
            case "include.package":
                logger.info("Include package value: {}", value);
                logger.info("If include.package is null or empty, all packages will be included.");
                break;
            default:
                throw new IllegalArgumentException("Unknown configuration key: " + key);
        }
        settings.put(key, value);
    }

    /**
     * Validates the loaded configuration to ensure all required keys are present.
     * 
     * Responsibilities:
     * - Checks for missing or invalid required keys.
     * 
     * Validated Keys:
     * - input.directory
     * - output.directory
     * - diagram.types
     * - include.package
     * 
     * @throws IllegalArgumentException if any required configuration is missing or invalid.
     * @since 1.0
     */
    public void validateConfig() {
        try {
            validateRequiredKeys();
            validateDiagramTypes();
            validateDirectories();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Configuration validation error: " + e.getMessage());
        }
        logger.debug("Configuration validated successfully.");
    }

    private void validateRequiredKeys() {
        List<String> requiredKeys = Arrays.asList(
                "input.directory",
                "output.directory",
                "diagram.types");

        for (String key : requiredKeys) {
            if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
                logger.error("Missing or empty required configuration: {}", key);
                throw new IllegalArgumentException("Missing or empty required configuration: " + key);
            }
        }
    }

    private void validateDiagramTypes() {
        String diagramTypes = settings.get("diagram.types");
        if (!diagramTypes
                .matches("(class|sequence|activity|state|usecase)(,(class|sequence|activity|state|usecase))*")) {
            logger.debug("Diagram types validation fails");
            throw new IllegalArgumentException("Invalid value for 'diagram.types': " + diagramTypes
                    + ". Expected values: class, sequence, activity, state, usecase.");
        }
    }

    private void validateDirectories() {
        String inputDir = settings.get("input.directory");
        String outputDir = settings.get("output.directory");
        logger.debug("Going to validate directories: input={}, output={}", inputDir, outputDir);

        if (inputDir == null || inputDir.isEmpty()) {
            logger.error("Input directory is not configured or is empty.");
            throw new IllegalArgumentException("Input directory is not configured or is empty.");
        }
        if (outputDir == null || outputDir.isEmpty()) {
            logger.error("Output directory is not configured or is empty.");
            throw new IllegalArgumentException("Output directory is not configured or is empty.");
        }

        validateDirectory(inputDir, "input.directory");
        validateDirectory(outputDir, "output.directory");
    }

    private void validateDirectory(String dirPath, String keyOrType) {
        File dir = new File(dirPath);

        if ("input.directory".equalsIgnoreCase(keyOrType)) {
            // Input directory validation
            if (!dir.exists() || !dir.isDirectory()) {
                logger.error("{} does not exist or is not a valid directory: {}", keyOrType, dirPath);
                throw new IllegalArgumentException(keyOrType + " must be a valid directory: " + dirPath);
            }
        } else if ("output.directory".equalsIgnoreCase(keyOrType)) {
            logger.debug("Going to create output directory");
            // Output directory validation
            if (!dir.exists()) {
                logger.debug("directory doesn't exists");
                if (!dir.mkdirs()) {
                    logger.error("{} does not exist and could not be created: {}", keyOrType, dirPath);
                    throw new IllegalArgumentException(
                            keyOrType + " does not exist and could not be created: " + dirPath);
                }
                logger.info("{} did not exist and was successfully created: {}", keyOrType, dirPath);
            }
            if (!dir.isDirectory()) {
                logger.error("{} is not a valid directory: {}", keyOrType, dirPath);
                throw new IllegalArgumentException(keyOrType + " is not a valid directory: " + dirPath);
            }
        } else {
            // For any other directory type, throw an exception
            throw new IllegalArgumentException("Unknown directory type: " + keyOrType);
        }
    }

    private void validateDiagramTypes(String types) {
        String[] validTypes = { "class", "sequence" };
        for (String type : types.split(",")) {
            if (!Arrays.asList(validTypes).contains(type.trim())) {
                throw new IllegalArgumentException("Invalid diagram type: " + type);
            }
        }
    }
}