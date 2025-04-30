package com.pjsoft.j2arch.config;

import java.io.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pjsoft.j2arch.core.util.ResourcePaths;

/**
 * ConfigurationLoader
 * 
 * Utility class for loading configuration properties from multiple sources.
 * This class provides a centralized mechanism for loading configuration files
 * and properties, ensuring that the application can retrieve settings from
 * various locations in a prioritized order.
 * 
 * Search Order:
 * 1. Java command-line argument (-Dconfig.file=path/to/file).
 * 2. Configuration directory (./config/application.properties).
 * 3. Classpath resource (config/application.properties).
 * 
 * If no configuration file is found, an empty Properties object is returned.
 * 
 * Responsibilities:
 * - Load configuration properties from various sources (file system, classpath, etc.).
 * - Provide fallback mechanisms for missing configuration files.
 * - Log the source of the loaded configuration for debugging purposes.
 * 
 * Limitations:
 * - Assumes that the configuration files are valid and accessible.
 * - Does not validate the correctness of the loaded properties.
 * 
 * Usage Example:
 * {@code
 * Properties properties = ConfigurationLoader.loadConfiguration();
 * String inputDir = properties.getProperty("input.directory");
 * System.out.println("Input Directory: " + inputDir);
 * }
 * 
 * Author: PJSoft
 * Version: 1.3
 * Since: 1.0
 */
public class ConfigurationLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

    /**
     * Loads configuration properties by searching in the following order:
     * 1. Java command-line argument (-Dconfig.file=path/to/file).
     * 2. Configuration directory (./config/application.properties).
     * 3. Classpath resource (config/application.properties).
     * 
     * Responsibilities:
     * - Attempt to load configuration from each source in the defined order.
     * - Return the first successfully loaded Properties object.
     * - Log warnings if no configuration file is found.
     * 
     * @return Properties object containing the loaded configuration.
     * @since 1.0
     */
    public static Properties loadConfiguration() {
        Properties properties = new Properties();

        // 1. Load from Java Command-Line Argument
        String configFilePath = System.getProperty(ResourcePaths.CUSTOM_CONFIGFILE_PROPERTY);
        if (configFilePath != null) {
            if (loadPropertiesFromFile(properties, new File(configFilePath))) {
                return properties;
            }
        }

        // 2. Load from Configuration Directory
        if (loadPropertiesFromFile(properties, new File(ResourcePaths.CUSTOM_CONFIG_FILE))) {
            return properties;
        }

        // 3. Load from Classpath Resource
        if (loadPropertiesFromClasspath(properties, ResourcePaths.DEFAULT_CONFIG_RESOURCE)) {
            return properties;
        }

        // 4. Return Empty Properties
        logger.warn("No configuration file found. Returning empty properties.");
        return properties;
    }

    /**
     * Loads properties from a classpath resource.
     * 
     * Responsibilities:
     * - Attempt to load properties from the specified classpath resource.
     * - Log the success or failure of the operation.
     * 
     * @param properties The Properties object to populate.
     * @param resource   The classpath resource to load properties from.
     * @return true if properties were successfully loaded, false otherwise.
     * @since 1.0
     */
    private static boolean loadPropertiesFromClasspath(Properties properties, String resource) {
        try (InputStream resourceStream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (resourceStream != null) {
                properties.load(resourceStream);
                logger.info("Loaded configuration from classpath resource: {}", resource);
                return true;
            } else {
                logger.debug("Classpath resource not found: {}", resource);
            }
        } catch (IOException e) {
            logger.error("Error loading properties from classpath resource: {}", resource, e);
        }
        return false;
    }

    /**
     * Loads properties from a file.
     * 
     * Responsibilities:
     * - Attempt to load properties from the specified file.
     * - Log the success or failure of the operation.
     * 
     * @param properties The Properties object to populate.
     * @param file       The file to load properties from.
     * @return true if properties were successfully loaded, false otherwise.
     * @since 1.0
     */
    private static boolean loadPropertiesFromFile(Properties properties, File file) {
        if (file.exists() && file.isFile()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                logger.info("Loaded configuration from: {}", file.getAbsolutePath());
                return true;
            } catch (IOException e) {
                logger.error("Error loading properties from file: {}", file.getAbsolutePath(), e);
            }
        } else {
            logger.debug("Configuration file not found: {}", file.getAbsolutePath());
        }
        return false;
    }

    /**
     * Loads properties from a specified configuration file.
     * 
     * Responsibilities:
     * - Validate the provided file path.
     * - Load properties from the specified file.
     * - Log the success or failure of the operation.
     * 
     * @param propertiesFile The path to the configuration file. If null or empty,
     *                       falls back to ResourcePaths.CUSTOM_CONFIG_FILE or the
     *                       system property "config.file".
     * @return A Properties object containing the loaded configuration.
     * @throws IOException If the configuration file is invalid or cannot be loaded.
     * @since 1.0
     */
    public static Properties loadProperties(String propertiesFile) throws IOException {
        // Step 1: Check if propertiesFile is provided
        if (propertiesFile == null || propertiesFile.trim().isEmpty()) {
            // Step 2: Fallback to the constant value
            propertiesFile = ResourcePaths.CUSTOM_CONFIG_FILE;

            // Step 3: If the constant is also empty, fallback to the environment variable
            if (propertiesFile == null || propertiesFile.trim().isEmpty()) {
                propertiesFile = System.getProperty(ResourcePaths.CUSTOM_CONFIGFILE_PROPERTY);
            }
        }

        // Step 4: Validate the configuration file
        if (propertiesFile == null || propertiesFile.trim().isEmpty()) {
            throw new IOException("Configuration file path is not defined.");
        }

        File configFile = new File(propertiesFile);
        if (!configFile.exists() || !configFile.isFile()) {
            throw new IOException("Configuration file not found: " + propertiesFile);
        }

        // Step 5: Load properties from the file
        Properties fileProperties = new Properties();
        try (var inputStream = new FileInputStream(configFile)) {
            fileProperties.load(inputStream);
            logger.info("Loaded properties from file: {}", propertiesFile);
        } catch (IOException e) {
            logger.error("Failed to load properties from file: {}", propertiesFile, e);
            throw e;
        }

        return fileProperties;
    }
}