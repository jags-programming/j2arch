package com.pjsoft.j2arch.gui.util;

/**
 * GUIStyleContext
 * 
 * Represents the context for GUI styling in the application. This class provides
 * paths to various theme stylesheets and the default style to be applied.
 * 
 * Responsibilities:
 * - Stores paths to dark, light, pastel, and common stylesheets.
 * - Provides access to the default style path to be used at startup.
 * 
 * Dependencies:
 * - None.
 * 
 * Limitations:
 * - Assumes that the provided paths are valid and accessible.
 * - Does not validate the existence or correctness of the stylesheet files.
 * 
 * Thread Safety:
 * - This class is immutable and therefore thread-safe.
 * 
 * Usage Example:
 * {@code
 * GUIStyleContext styleContext = new GUIStyleContext(
 *     "/styles/dark-theme.css",
 *     "/styles/light-theme.css",
 *     "/styles/pastel-theme.css",
 *     "/styles/common.css",
 *     "/styles/light-theme.css"
 * );
 * String defaultStyle = styleContext.getDefaultStylePath();
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class GUIStyleContext {
    private final String darkThemePath; // Path to the dark theme stylesheet
    private final String lightThemePath; // Path to the light theme stylesheet
    private final String pastelThemePath; // Path to the pastel theme stylesheet
    private final String commonStylePath; // Path to the common stylesheet
    private final String defaultStylePath; // Path to the default theme stylesheet

    /**
     * Constructs a new GUIStyleContext.
     * 
     * Responsibilities:
     * - Initializes the paths to various theme stylesheets and the default style.
     * 
     * @param darkThemePath   Path to the dark theme stylesheet.
     * @param lightThemePath  Path to the light theme stylesheet.
     * @param pastelThemePath Path to the pastel theme stylesheet.
     * @param commonStylePath Path to the common stylesheet.
     * @param defaultStylePath Path to the default theme stylesheet.
     */
    public GUIStyleContext(String darkThemePath, String lightThemePath, String pastelThemePath, 
                           String commonStylePath, String defaultStylePath) {
        this.darkThemePath = darkThemePath;
        this.lightThemePath = lightThemePath;
        this.pastelThemePath = pastelThemePath;
        this.commonStylePath = commonStylePath;
        this.defaultStylePath = defaultStylePath;
    }

    /**
     * Retrieves the path to the dark theme stylesheet.
     * 
     * @return The path to the dark theme stylesheet.
     */
    public String getDarkThemePath() {
        return darkThemePath;
    }

    /**
     * Retrieves the path to the light theme stylesheet.
     * 
     * @return The path to the light theme stylesheet.
     */
    public String getLightThemePath() {
        return lightThemePath;
    }

    /**
     * Retrieves the path to the pastel theme stylesheet.
     * 
     * @return The path to the pastel theme stylesheet.
     */
    public String getPastelThemePath() {
        return pastelThemePath;
    }

    /**
     * Retrieves the path to the common stylesheet.
     * 
     * @return The path to the common stylesheet.
     */
    public String getCommonStylePath() {
        return commonStylePath;
    }

    /**
     * Retrieves the path to the default theme stylesheet.
     * 
     * @return The path to the default theme stylesheet.
     */
    public String getDefaultStylePath() {
        return defaultStylePath;
    }
}
