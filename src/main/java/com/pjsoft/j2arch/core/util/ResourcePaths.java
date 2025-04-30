package com.pjsoft.j2arch.core.util;

/**
 * ResourcePaths
 * 
 * Defines constants for resource file paths used throughout the application.
 * These constants include paths for configuration files, HTML templates, and
 * stylesheets for both Javadoc and HTML documentation generation.
 * 
 * Responsibilities:
 * - Provide paths for configuration files, including default and custom configurations.
 * - Provide paths for HTML templates used in documentation generation.
 * - Provide paths for stylesheets used in Javadoc and HTML documentation.
 * 
 * Limitations:
 * - Assumes that the specified resource paths exist and are accessible in the project structure.
 * - Does not validate the correctness or existence of the resource files.
 * 
 * Usage Example:
 * {@code
 * String configPath = ResourcePaths.CUSTOM_CONFIG_FILE;
 * String javadocIndexTemplate = ResourcePaths.TEMPLATE_JAVADOC_INDEX;
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class ResourcePaths {
    // Configuration file paths
    public static final String CUSTOM_CONFIG_FILE = "config/application.properties"; // Path to custom configuration file
    public static final String CUSTOM_CONFIGFILE_PROPERTY = "config.file"; // System property for custom config file
    public static final String DEFAULT_CONFIG_RESOURCE = "/config/application.properties"; // Default configuration file

    // HTML documentation template paths
    public static final String TEMPLATE_HTML_DOC_DIAGRAM = "/templates/htmldoc/diagram.html"; // Diagram page template
    public static final String TEMPLATE_HTML_DOC_INDEX = "/templates/htmldoc/index.html"; // Index page template
    public static final String TEMPLATE_STYLE_HTML_DOC = "/styles/htmldoc/style_htmldoc.css"; // HTML doc stylesheet

    // Javadoc template paths
    public static final String TEMPLATE_JAVADOC_INDEX = "/templates/javadoc/index.html"; // Javadoc index page template
    public static final String TEMPLATE_JAVADOC_CLASS = "/templates/javadoc/class.html"; // Javadoc class page template
    public static final String TEMPLATE_JAVADOC_PACKAGE = "/templates/javadoc/package.html"; // Javadoc package page template
    public static final String TEMPLATE_STYLE_JAVADOC = "/styles/javadoc/style_javadoc.css"; // Javadoc stylesheet
}
