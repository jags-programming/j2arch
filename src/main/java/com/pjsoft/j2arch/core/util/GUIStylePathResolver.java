package com.pjsoft.j2arch.core.util;

import java.io.File;

import com.pjsoft.j2arch.gui.ConfigurationTab;

/**
 * GUIStylePathResolver
 * 
 * Utility class for resolving style paths dynamically. This class checks the file system
 * first for the provided style path and falls back to the classpath if the file is not found.
 * It ensures that the correct style path is resolved for GUI components.
 * 
 * Responsibilities:
 * - Resolve style paths from the file system or classpath.
 * - Handle paths with "file:" prefixes and regular file paths.
 * - Log debug information about the resolved paths.
 * 
 * Limitations:
 * - Assumes that the provided style path is valid and accessible.
 * - Throws an {@link IllegalArgumentException} if the style path cannot be resolved.
 * 
 * Usage Example:
 * {@code
 * String resolvedPath = GUIStylePathResolver.resolveStylePath("/styles/gui/style_gui_dark.css");
 * System.out.println(resolvedPath);
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class GUIStylePathResolver {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigurationTab.class);

    /**
     * Resolves the style path dynamically.
     * 
     * Responsibilities:
     * - Checks the file system first for the provided style path.
     * - Falls back to the classpath if the file is not found in the file system.
     * - Handles paths with "file:" prefixes and regular file paths.
     * 
     * @param stylePath The style path from the properties file.
     * @return The resolved path as a URI or classpath resource.
     * @throws IllegalArgumentException if the style path cannot be resolved.
     */
    public static String resolveStylePath(String stylePath) {
        try {
            // Handle paths with "file:" prefix
            if (stylePath.startsWith("file:")) {
                File styleFile = new File(new java.net.URI(stylePath));

                if (styleFile.exists() && styleFile.isFile()) {
                    logger.debug("Style path resolved from file system: {}", styleFile.getAbsolutePath());
                    return styleFile.toURI().toString();
                }
            } else {
                // Handle regular file system paths
                File styleFile = new File(stylePath);

                if (styleFile.exists() && styleFile.isFile()) {
                    logger.debug("Style path resolved from file system: {}", styleFile.getAbsolutePath());
                    return styleFile.toURI().toString();
                }
            }
        } catch (Exception e) {
            logger.error("Error resolving file path: " + e.getMessage());
        }

        // Fallback to classpath
        String classpathStylePath = stylePath.startsWith("/") ? stylePath : "/" + stylePath;
        var resource = GUIStylePathResolver.class.getResource(classpathStylePath);

        if (resource != null) {
            logger.debug("Style path resolved from classpath: {}", resource.toExternalForm());
            return resource.toExternalForm();
        }

        throw new IllegalArgumentException("Style path not found: " + stylePath);
    }
}