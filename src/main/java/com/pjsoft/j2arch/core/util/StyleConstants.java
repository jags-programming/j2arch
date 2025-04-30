package com.pjsoft.j2arch.core.util;

/**
 * StyleConstants
 * 
 * Defines constants for stylesheet paths used throughout the application. These
 * constants include paths for GUI themes and output stylesheets for generated
 * documentation.
 * 
 * Responsibilities:
 * - Provide paths for GUI theme stylesheets (dark, light, pastel, and common styles).
 * - Provide paths for output stylesheets used in Javadoc and HTML documentation generation.
 * 
 * Limitations:
 * - Assumes that the specified stylesheet paths exist and are accessible in the project structure.
 * - Does not validate the correctness of the stylesheet content.
 * 
 * Usage Example:
 * {@code
 * String darkThemePath = StyleConstants.DEFAULT_GUI_DARK_STYLE;
 * String javadocStylePath = StyleConstants.OUTPUT_JAVADOC_STYLE;
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class StyleConstants {
    // Paths for GUI theme stylesheets
    public static final String DEFAULT_GUI_DARK_STYLE = "/styles/gui/style_gui_dark.css"; // Dark theme
    public static final String DEFAULT_GUI_LIGHT_STYLE = "/styles/gui/style_gui_light.css"; // Light theme
    public static final String DEFAULT_GUI_PASTEL_STYLE = "/styles/gui/style_gui_pastel.css"; // Pastel theme
    public static final String DEFAULT_GUI_COMMON_STYLE = "/styles/gui/style_gui_common.css"; // Common styles
    public static final String DEFAULT_GUI_DEFAULT_STYLE = "/styles/gui/style_gui_light.css"; // Default theme

    // Paths for output stylesheets used in documentation generation
    public static final String OUTPUT_JAVADOC_STYLE = "styles/style_javadoc.css"; // Javadoc stylesheet
    public static final String OUTPUT_HTMLDOC_STYLE = "styles/style_htmldoc.css"; // HTML documentation stylesheet
}
