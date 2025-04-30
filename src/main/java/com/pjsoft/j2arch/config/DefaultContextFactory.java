package com.pjsoft.j2arch.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.util.Properties;

import com.pjsoft.j2arch.core.util.DirectoryConstants;
import com.pjsoft.j2arch.core.util.GUIStylePathResolver;
import com.pjsoft.j2arch.core.util.PathResolver;
import com.pjsoft.j2arch.core.util.ResourcePaths;
import com.pjsoft.j2arch.core.util.StyleConstants;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.pumldoc.util.HTMLGenerationContext;
import com.pjsoft.j2arch.gui.util.GUIStyleContext;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * DefaultContextFactory
 * 
 * A factory class for creating various context objects used in the application.
 * This class provides methods to create contexts for UML generation, HTML generation,
 * JavaDoc generation, and GUI styling. It uses configuration properties to initialize
 * these contexts with appropriate paths and settings.
 * 
 * Responsibilities:
 * - Create and configure UMLGenerationContext, HTMLGenerationContext, JavaDocGenerationContext, and GUIStyleContext.
 * - Load configuration properties and resolve paths for input, output, templates, and styles.
 * - Provide static methods for creating contexts from configuration files.
 * 
 * Limitations:
 * - Assumes that the provided properties contain valid paths and settings.
 * - Does not validate the correctness of the paths or settings in the properties.
 * 
 * Usage Example:
 * {@code
 * Properties properties = ConfigurationLoader.loadProperties("config.properties");
 * DefaultContextFactory factory = new DefaultContextFactory(properties);
 * UMLGenerationContext umlContext = factory.createUMLContext();
 * }
 * 
 * Author: PJSoft
 * Version: 1.3
 * Since: 1.0
 */
public class DefaultContextFactory implements ContextFactory {
    private final Properties properties;

    /**
     * Constructs a DefaultContextFactory with the specified properties.
     * 
     * @param properties The configuration properties to use for creating contexts.
     * @since 1.0
     */
    public DefaultContextFactory(Properties properties) {
        this.properties = properties;
    }

    @Override
    public JavaDocGenerationContext createJavaDocContext() {
        return new JavaDocGenerationContext(
                properties.getProperty("input.javadoc.directory", DirectoryConstants.DEFAULT_INPUT_DIR),
                properties.getProperty("output.javadoc.directory", DirectoryConstants.DEFAULT_OUTPUT_JAVADOC_DIR),
                properties.getProperty("template.javadoc.index", ResourcePaths.TEMPLATE_JAVADOC_INDEX),
                properties.getProperty("template.javadoc.class", ResourcePaths.TEMPLATE_JAVADOC_CLASS),
                properties.getProperty("template.javadoc.package", ResourcePaths.TEMPLATE_JAVADOC_PACKAGE),
                properties.getProperty("template.style.javadoc", ResourcePaths.TEMPLATE_STYLE_JAVADOC),
                properties.getProperty("include.package", ""), // Optional include.package property
                properties.getProperty("libs.dirpath", PathResolver.resolvePath(DirectoryConstants.DEFAULT_INPUT_DIR, DirectoryConstants.LIBS_DIR)));
    }

    @Override
    public JavaDocGenerationContext createJavaDocContext(String inputDirectory, String outputDirectory,
            String indexTemplate, String classTemplate, String packageTemplate, String styleSourceFile,
            String includePackage, String libsDirPath) {
        return new JavaDocGenerationContext(
                inputDirectory,
                outputDirectory,
                indexTemplate,
                classTemplate,
                packageTemplate,
                styleSourceFile,
                includePackage,
                libsDirPath);
    }

    @Override
    public UMLGenerationContext createUMLContext() {
        return new UMLGenerationContext(
                properties.getProperty("input.uml.directory", DirectoryConstants.DEFAULT_INPUT_DIR),
                properties.getProperty("output.uml.directory", DirectoryConstants.DEFAULT_OUTPUT_UMLDOC_DIR),
                properties.getProperty("diagram.types", "class,sequence"),
                properties.getProperty("include.package", ""),
                properties.getProperty("output.uml.puml", PathResolver.resolvePath(DirectoryConstants.DEFAULT_OUTPUT_UMLDOC_DIR, DirectoryConstants.PUML_DIR)),
                properties.getProperty("output.uml.images", PathResolver.resolvePath(DirectoryConstants.DEFAULT_OUTPUT_UMLDOC_DIR, DirectoryConstants.IMAGES_DIR)),
                properties.getProperty("output.uml.unified.classdiagram", DirectoryConstants.DEFAULT_UNIFIED_CLASS_DIAGRAM),
                properties.getProperty("libs.dirpath", PathResolver.resolvePath(DirectoryConstants.DEFAULT_INPUT_DIR, DirectoryConstants.LIBS_DIR)));
    }

    @Override
    public UMLGenerationContext createUMLContext(String inputDirectory, String outputDirectory, String diagramTypes,
            String includePackage, String pumlPath, String imagesOutputPath,
            String unifiedClassDiagram, String libsDirPath) {
        return new UMLGenerationContext(
                inputDirectory,
                outputDirectory,
                diagramTypes,
                includePackage,
                pumlPath,
                imagesOutputPath,
                unifiedClassDiagram,
                libsDirPath);
    }

    @Override
    public HTMLGenerationContext createHTMLContext() {
        return new HTMLGenerationContext(
                properties.getProperty("input.htmldoc.directory", PathResolver.resolvePath(DirectoryConstants.DEFAULT_OUTPUT_UMLDOC_DIR, DirectoryConstants.PUML_DIR)),
                properties.getProperty("output.htmldoc.directory", DirectoryConstants.DEFAULT_OUTPUT_HTMLDOC_DIR),
                properties.getProperty("template.htmldoc.diagram", ResourcePaths.TEMPLATE_HTML_DOC_DIAGRAM),
                properties.getProperty("template.htmldoc.index", ResourcePaths.TEMPLATE_HTML_DOC_INDEX),
                properties.getProperty("template.style.htmldoc", ResourcePaths.TEMPLATE_STYLE_HTML_DOC));
    }

    @Override
    public HTMLGenerationContext createHTMLContext(
            String inputDirectory,
            String outputDirectory,
            String diagramTemplateFile,
            String indexTemplateFile,
            String styleSourceFile) {

        return new HTMLGenerationContext(inputDirectory, outputDirectory,
                diagramTemplateFile,
                indexTemplateFile, styleSourceFile);
    }

    @Override
    public GUIStyleContext createGUIStyleContext() {
        return new GUIStyleContext(
                GUIStylePathResolver.resolveStylePath(properties.getProperty("style.gui.dark", StyleConstants.DEFAULT_GUI_DARK_STYLE)),
                GUIStylePathResolver.resolveStylePath(properties.getProperty("style.gui.light", StyleConstants.DEFAULT_GUI_LIGHT_STYLE)),
                GUIStylePathResolver.resolveStylePath(properties.getProperty("style.gui.pastel", StyleConstants.DEFAULT_GUI_PASTEL_STYLE)),
                GUIStylePathResolver.resolveStylePath(properties.getProperty("style.gui.common", StyleConstants.DEFAULT_GUI_COMMON_STYLE)),
                GUIStylePathResolver.resolveStylePath(properties.getProperty("style.gui.default", StyleConstants.DEFAULT_GUI_DEFAULT_STYLE))
        );
    }

    /**
     * Static method to create a UMLGenerationContext from a configuration file.
     * 
     * Responsibilities:
     * - Load configuration properties from the specified file.
     * - Use the loaded properties to create a UMLGenerationContext.
     * 
     * @param propertiesFile The path to the configuration file.
     * @return A UMLGenerationContext created from the configuration file.
     * @throws IOException If the configuration file is invalid or cannot be loaded.
     * @since 1.0
     */
    public static UMLGenerationContext getContextFromFile(String propertiesFile) throws IOException {
        // Load properties using ConfigurationLoader
        Properties fileProperties = ConfigurationLoader.loadProperties(propertiesFile);

        // Use DefaultContextFactory to create UMLGenerationContext
        DefaultContextFactory contextFactory = new DefaultContextFactory(fileProperties);
        return contextFactory.createUMLContext();
    }
}
